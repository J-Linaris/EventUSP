import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { Carousel } from 'react-responsive-carousel';
import "react-responsive-carousel/lib/styles/carousel.min.css";
import Navbar from "../components/NavBar.tsx";
import "./basicStyle.css";
import "./PaginaEvento.css"
import { useAuth } from "../context/AuthContext.tsx";

// Reutilize as mesmas interfaces (idealmente, mova-as para um arquivo /types/evento.ts)
interface ImagemEvento {
    id: number;
    url: string;
    descricao?: string;
    ordem: number;
}
interface Organizador {
    id: number;
    nome: string;
    email: string;
    fotoPerfil?: string;
}

interface Review {
    id: number;
    eventoId: number;
    participanteId: number; // Supondo que o backend possa fornecer esses dados
    nota: number;
    comentario: string;
    dataHora?: string; // Opcional, mas bom ter
}

interface Evento {
    id: number;
    titulo: string;
    descricao: string;
    dataHora: string;
    localizacao: string;
    categoria: string;
    organizador: Organizador;
    numeroLikes: number;
    imagens: ImagemEvento[];
    reviews?: Review[]; // Adicionamos o campo de reviews como opcional
}

// --- Novo Componente para um Item de Review Individual ---
// Este componente recebe uma review e busca o nome do autor por conta própria.
function ReviewItem({ review }: { review: Review }) {
    const [authorName, setAuthorName] = useState<string>("Carregando autor...");

    useEffect(() => {
        const fetchAuthorName = async () => {
            try {
                // Rota baseada no seu arquivo Routing.kt: /api/participantes/{id}
                const response = await fetch(`/proxy/api/participantes/${review.participanteId}`);
                if (!response.ok) {
                    throw new Error("Participante não encontrado.");
                }
                const participanteData = await response.json();
                setAuthorName(participanteData.nome);
            } catch (error) {
                console.error("Erro ao buscar nome do participante:", error);
                setAuthorName("Participante Anônimo"); // Fallback em caso de erro
            }
        };

        fetchAuthorName();
    }, [review.participanteId]); // Executa apenas quando o ID do participante muda

    return (
        <div className="review-item">
            <p className="review-nota"><strong>Nota:</strong> {"⭐".repeat(review.nota)}</p>
            <p className="review-comentario">"{review.comentario}"</p>
            <span className="review-author">- {authorName}</span>
        </div>
    );
}

function PaginaEvento() {
    const { id } = useParams<{ id: string }>();
    const [evento, setEvento] = useState<Evento | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    //Estados para o Modal de Review ---
    const [isReviewModalOpen, setReviewModalOpen] = useState(false);
    const [nota, setNota] = useState(0);
    const [comentario, setComentario] = useState("");
    const [reviewError, setReviewError] = useState<string | null>(null);
    const [isLiking, setIsLiking] = useState(false);

    // const [timeLeft, setTimeLeft] = useState<string>('');
    // const [showLikeSection, setShowLikeSection] = useState<boolean>(false);

    const { user } = useAuth();

    useEffect(() => {
        if (!id) return; // Se não houver ID, não faz nada

        const fetchEventoCompleto = async () => {
            try {
                setLoading(true);

                // Prepara as duas requisições para serem executadas em paralelo
                const eventoPromise = fetch(`/proxy/api/eventos/${id}`);
                const imagensPromise = fetch(`/proxy/api/eventos/${id}/imagens`);

                // Executa ambas as requisições simultaneamente
                const [eventoResponse, imagensResponse] = await Promise.all([
                    eventoPromise,
                    imagensPromise,
                ]);

                if (!eventoResponse.ok) {
                    throw new Error('Evento não encontrado ou erro no servidor.');
                }

                // 3. Processa os resultados
                const eventoData = await eventoResponse.json();

                // Se a busca de imagens falhar, usa um array vazio como padrão
                const imagensData = imagensResponse.ok ? await imagensResponse.json() : [];

                // 4. Combina os dados e ordena as imagens
                imagensData.sort((a: ImagemEvento, b: ImagemEvento) => a.ordem - b.ordem);

                const eventoCompleto = { ...eventoData, imagens: imagensData };
                if (!eventoCompleto.numeroLikes) {
                    eventoCompleto.numeroLikes = 0;
                }
                setEvento(eventoCompleto);

            } catch (err) {
                if (err instanceof Error) {
                    setError(err.message);
                } else {
                    setError("Ocorreu um erro desconhecido");
                }
            } finally {
                setLoading(false);
            }
        };

        fetchEventoCompleto();
    }, [id]); // Executa o efeito sempre que o ID na URL mudar
    // --- Função para submeter a nova review ---
    const handleReviewSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setReviewError(null);

        const token = localStorage.getItem('authToken'); // Pega o token do usuário logado
        if (!token) {
            setReviewError("Você precisa estar logado para deixar uma review.");
            return;
        }

        if (nota === 0 || comentario.trim() === "") {
            setReviewError("Por favor, dê uma nota e escreva um comentário.");
            return;
        }

        try {
            const response = await fetch(`/proxy/api/eventos/${id}/reviews`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({
                    nota: nota,
                    comentario: comentario
                })
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || "Não foi possível enviar a review.");
            }

            // Sucesso! Fecha o modal e atualiza a UI.
            setReviewModalOpen(false);
            setNota(0);
            setComentario("");

            // Para uma melhor UX, podemos adicionar a review diretamente ao estado
            // ou simplesmente recarregar os dados do evento.
            // Recarregar é mais simples:
            const updatedEventoResponse = await fetch(`/proxy/api/eventos/${id}`);
            const updatedEventoData = await updatedEventoResponse.json();
            setEvento(prev => ({...prev, ...updatedEventoData}));

            alert("Review enviada com sucesso!");

        } catch (err) {
            if (err instanceof Error) {
                setReviewError(err.message);
            } else {
                setReviewError("Ocorreu um erro desconhecido ao enviar a review.");
            }
        }
    };

    const handleLike = async () => {
        if (!evento || isLiking) return; // Não faz nada se não houver evento ou já estiver processando um like

        const token = localStorage.getItem('authToken');
        if (!token) {
            alert("Você precisa estar logado para curtir um evento.");
            return;
        }

        setIsLiking(true); // Desabilita o botão para evitar cliques duplos
        const payload = {
            id: evento.id,
            titulo: evento.titulo,
            descricao: evento.descricao,
            dataHora: evento.dataHora,
            localizacao: evento.localizacao,
            categoria: evento.categoria,
            organizador: evento.organizador,
            // O campo 'participantesInteressados' pode ser necessário pelo backend,
            // mesmo que vazio. Se ele não for enviado, também pode causar um erro 400.
            // participantesInteressados: evento.participantesInteressados || [],
            numeroLikes: evento.numeroLikes + 1,
        };

        // 1. Cria uma cópia do objeto evento e incrementa o número de likes
        // const updatedEvento = {
        //     ...evento,
        //     numeroLikes: evento.numeroLikes + 1,
        // };

        try {
            // 2. Envia o payload limpo para o endpoint PUT
            const response = await fetch(`/proxy/api/eventos/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
                body: JSON.stringify(payload), // Envia o objeto limpo
            });

            if (!response.ok) {
                // Para depurar, você pode tentar ver o corpo da resposta de erro
                const errorBody = await response.text();
                console.error("Erro do servidor:", errorBody);
                throw new Error("Não foi possível registrar o like. Verifique o console para mais detalhes.");
            }

            // 3. Atualiza o estado com a resposta do servidor
            const responseData = await response.json();

            // Atualiza o estado preservando as imagens e reviews que já estavam carregadas
            setEvento(prevEvento => ({
                ...prevEvento,
                ...responseData,
            }));

        } catch (err) {
            console.error(err);
            alert(err instanceof Error ? err.message : "Ocorreu um erro.");
        } finally {
            setIsLiking(false);
        }
    };

    if (loading) return <p>Carregando evento...</p>;
    if (error) return <p>Erro ao carregar evento: {error}</p>;
    if (!evento) return <p>Evento não encontrado.</p>;

    return (
        <>
            <div className="evento-page">
                <Navbar />
                <div className="card-evento-detalhe">
                    {evento.imagens && evento.imagens.length > 0 && (
                        <Carousel showThumbs={false} infiniteLoop useKeyboardArrows autoPlay>
                            {evento.imagens.map((imagem) => (
                                <div key={imagem.id}>
                                    <img src={imagem.url} alt={imagem.descricao || evento.titulo} className="imgsEventoDetalhes"/>
                                    {/*{imagem.descricao && <p className="legend">{imagem.descricao}</p>}*/}
                                </div>
                            ))}
                        </Carousel>
                    )}
                    <div className="card-evento-body">
                        <h1>{evento.titulo}</h1>
                        <p>
                            <strong>Descrição:</strong><br/>
                            {evento.descricao}
                        </p>
                        <p><strong>Data:</strong> <br/>{new Date(evento.dataHora).toLocaleString()}</p>
                        <p><strong>Local:</strong> <br/>{evento.localizacao}</p>
                        <p><strong>Categoria:</strong> <br/>{evento.categoria}</p>
                        <div><strong>Organizador:</strong> <br/>
                            <div className="infosOrganizadorEvento">
                                <img src={evento.organizador.fotoPerfil} alt={`Foto de perfil de ${evento.organizador.nome}`} className="fotoPerfilOrganizador"/><span>{evento.organizador.nome}</span>
                            </div>
                        </div>
                        <div className="likes-section">
                            <div className="likes-num-container">
                                <strong>Likes:</strong>
                                <span className="likesEvento">{evento.numeroLikes || 0}</span>
                            </div>
                            <span className="likes-button-container">
                                {/* O botão de Like só aparece se o usuário for um participante */}
                                {user && user.accountType === 'participante' && (
                                    <button onClick={handleLike} disabled={isLiking} className="btn-like">
                                        {isLiking ? '👍' : '👍'}
                                    </button>
                                )}
                            </span>
                        </div>
                        <div className="reviews-section">
                            <div className="reviews-header">
                                <h2>Reviews</h2>
                                {/* 4. O botão de Adicionar Review só aparece se o usuário for participante e não houver reviews */}
                                {user && user.accountType === 'participante' && (!evento.reviews || evento.reviews.length === 0) && (
                                    <button onClick={() => setReviewModalOpen(true)} className="btn-add-review">
                                        Adicionar Review
                                    </button>
                                )}
                            </div>

                            {evento.reviews && evento.reviews.length > 0 ? (
                                <div className="reviews-list">
                                    {/* Mapeia cada review para o novo componente ReviewItem */}
                                    {evento.reviews.map(review => (
                                        <ReviewItem key={review.id} review={review} />
                                    ))}
                                </div>
                            ) : (
                                // Se não houver reviews, verificamos o tipo de usuário aqui:
                                // A condição é: "O usuário está logado E seu papel é 'participante'?"
                                user && user.accountType === 'organizador' ? (
                                    <p>Nenhuma review ainda :(</p>
                                ) : ( user && user.accountType === 'participante' ? (
                                        <p>Nenhuma review ainda. Deixe a sua!</p>
                                    ) : (
                                        <p>Nenhuma review ainda, logue para deixar sua review</p>
                                    )
                                )
                            )}
                        </div>
                    </div>
                </div>
            </div>

            {/* --- Modal para Adicionar Review --- */}
            {isReviewModalOpen && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Deixe sua Review</h2>
                        <form onSubmit={handleReviewSubmit}>
                            <div className="form-group">
                                <label>Nota (1-5):</label>
                                <div className="star-rating">
                                    {[1, 2, 3, 4, 5].map(star => (
                                        <span
                                            key={star}
                                            className={star <= nota ? 'star-filled' : 'star-empty'}
                                            onClick={() => setNota(star)}
                                        >
                                            ⭐
                                        </span>
                                    ))}
                                </div>
                            </div>
                            <div className="form-group">
                                <label htmlFor="comentario">Comentário:</label>
                                <textarea
                                    id="comentario"
                                    value={comentario}
                                    onChange={(e) => setComentario(e.target.value)}
                                    required
                                />
                            </div>
                            {reviewError && <p className="error-message">{reviewError}</p>}
                            <div className="modal-actions">
                                <button type="button" onClick={() => setReviewModalOpen(false)} className="btn-cancel">Cancelar</button>
                                <button type="submit" className="btn-submit">Enviar Review</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </>
    );
}

export default PaginaEvento;