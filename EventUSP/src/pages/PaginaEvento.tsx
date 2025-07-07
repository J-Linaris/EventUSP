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
    participanteId: number;
    nota: number;
    comentario: string;
    dataHora?: string;
}
interface Participante { // Interface simplificada para a lista de interessados
    id: number;
    nome: string;
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
    reviews?: Review[];
    participantesInteressados?: Participante[]; // Usando a interface Participante
}
// Tipagem para o usuário logado, vindo do AuthContext
interface CurrentUser {
    id: number;
    accountType: string;
    // adicione outros campos do usuário se necessário
}

// --- Novo Componente para um Item de Review Individual ---
// Este componente recebe uma review e busca o nome do autor por conta própria.
function ReviewItem({ review, currentUser, onDelete }: { review: Review, currentUser: CurrentUser | null, onDelete: (reviewId: number) => void }) {
    const [authorName, setAuthorName] = useState<string>("Carregando autor...");
    useEffect(() => {
        const fetchAuthorName = async () => {
            try {
                const response = await fetch(`/proxy/api/participantes/${review.participanteId}`);
                if (!response.ok) {
                    throw new Error("Participante não encontrado.");
                }
                const participanteData = await response.json();
                setAuthorName(participanteData.nome);
            } catch (error) {
                console.error("Erro ao buscar nome do participante:", error);
                setAuthorName("Participante Anônimo");
            }
        };

        fetchAuthorName();
    }, [review.participanteId]);

    // Verifica se o usuário logado é o autor da review
    const isOwner = currentUser?.id === review.participanteId;

    return (
        <div className="review-item">
            <div className="review-header-item">
                <p className="review-nota"><strong>Nota:</strong> {"⭐".repeat(review.nota)}</p>
                {/* O botão de excluir só aparece se o usuário for o dono da review */}
                {isOwner && (
                    <button onClick={() => onDelete(review.id)} className="btn-delete-review" title="Excluir review">
                        ❌
                    </button>
                )}
            </div>
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
    const [isHovering, setIsHovering] = useState(false);
    const { user, authFetch } = useAuth();
    // Lógica para verificar se o usuário atual já fez uma review
    const usuarioJaFezReview = evento?.reviews?.some(r => r.participanteId === user?.id) || false;
    // Deriva o estado de interesse diretamente do objeto 'evento'. Esta é a única fonte de verdade.
    const jaTemInteresse = evento?.participantesInteressados?.some(p => p.id === user?.id) || false;
    // --- NOVO ESTADO PARA O HOVER DAS ESTRELAS ---
    const [hoveredNota, setHoveredNota] = useState<number | null>(null);

    useEffect(() => {
        if (!id) return; // Se não houver ID, não faz nada

        const fetchEventoData = async () => {
            try {
                setLoading(true);

                // Prepara as duas requisições para serem executadas em paralelo
                const eventoPromise = fetch(`/proxy/api/eventos/${id}`);
                const imagensPromise = fetch(`/proxy/api/eventos/${id}/imagens`);
                const reviewsPromise = fetch(`/proxy/api/eventos/${id}/reviews`);

                // Executa ambas as requisições simultaneamente
                const [eventoResponse, imagensResponse, reviewsResponse] = await Promise.all([
                    eventoPromise,
                    imagensPromise,
                    reviewsPromise
                ]);

                if (!eventoResponse.ok) {
                    throw new Error('Evento não encontrado ou erro no servidor.');
                }

                // 3. Processa os resultados
                const eventoData = await eventoResponse.json();
                // (Se a busca falhar, usa um array vazio como padrão)
                const imagensData = imagensResponse.ok ? await imagensResponse.json() : [];
                const reviewsData = reviewsResponse.ok ? await reviewsResponse.json() : [];

                //

                // 4. Combina os dados e ordena as imagens
                imagensData.sort((a: ImagemEvento, b: ImagemEvento) => a.ordem - b.ordem);

                const eventoCompleto = { ...eventoData, imagens: imagensData, reviews: reviewsData };

                if (!eventoCompleto.numeroLikes) {
                    eventoCompleto.numeroLikes = 0;
                }
                // Garante que a lista de participantes exista
                if (!eventoCompleto.participantesInteressados) eventoCompleto.participantesInteressados = [];

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

        fetchEventoData();
    }, [id]); // Executa o efeito sempre que o ID na URL mudar

    // // Efeito separado para verificar o status de interesse do usuário logado
    // useEffect(() => {
    //     // Se o usuário está logado e já temos os dados do evento
    //     if (user && evento) {
    //         const userJaTemInteresse = evento.participantesInteressados?.some(
    //             (p: any) => p.id === user.id
    //         ) || false;
    //         setJaTemInteresse(userJaTemInteresse);
    //     }
    // }, [user, evento]);

    // --- Função para submeter a nova review ---
    // --- Função para submeter a nova review ---
    const handleReviewSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setReviewError(null);

        if (!user) { // Verifica o usuário pelo contexto
            setReviewError("Você precisa estar logado para deixar uma review.");
            return;
        }
        if (nota === 0 || comentario.trim() === "") {
            setReviewError("Por favor, dê uma nota e escreva um comentário.");
            return;
        }

        try {
            // USA authFetch: Ele já adiciona o 'Authorization' e o 'Content-Type'
            const response = await authFetch(`/proxy/api/eventos/${id}/reviews`, {
                method: 'POST',
                body: JSON.stringify({nota, comentario})
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || "Não foi possível enviar a review.");
            }

            const novaReview = await response.json();
            // Atualiza o estado local para a UI refletir a mudança imediatamente
            setEvento(prev => prev ? {...prev, reviews: [...(prev.reviews || []), novaReview]} : null);
            setReviewModalOpen(false);

        } catch (err) {
            setReviewError(err instanceof Error ? err.message : "Ocorreu um erro desconhecido.");
        }
    };

    const handleInteresse = async () => {
        if (!user) { // Verifica se o usuário está logado usando o contexto
            alert("Você precisa estar logado para demonstrar interesse.");
            return;
        }
        // if (isLiking || jaTemInteresse) return;
        if (isLiking) return;

        setIsLiking(true);

        // Define o método e a mudança de likes com base no estado atual
        const originalEvento = evento; // Salva o estado original para rollback em caso de erro
        const method = jaTemInteresse ? 'DELETE' : 'POST';

        // 1. Atualização Otimista do número de likes mudando o evento em si: Mude a UI imediatamente.
        if (jaTemInteresse) {
            // Se já tem interesse, remove o usuário da lista e decrementa o like
            setEvento(prev => prev ? {
                ...prev,
                numeroLikes: prev.numeroLikes - 1,
                participantesInteressados: prev.participantesInteressados?.filter(p => p.id !== user.id)
            } : null);
        } else {
            // Se não tem interesse, adiciona o usuário à lista e incrementa o like
            setEvento(prev => prev ? {
                ...prev,
                numeroLikes: prev.numeroLikes + 1,
                participantesInteressados: [...(prev.participantesInteressados || []), { id: user.id, nome: user.nome }]
            } : null);
        }

        try {
            // 2. Chama a API
            const response = await authFetch(`/proxy/api/eventos/${id}/interesse`, {
                method: method,
            });

            if (!response.ok) {
                // Se a API falhar, lança um erro para o bloco catch
                throw new Error("A operação falhou no servidor. Tente novamente.");
            }
            // Se a API for bem-sucedida, a atualização otimista se torna o estado final.
        } catch (err) {
            // 3. Rollback: Se a API falhar, reverta a UI para o estado original
            alert(err instanceof Error ? err.message : "Ocorreu um erro.");
            setEvento(originalEvento);
        } finally {
            setIsLiking(false);
        }
    };

    // --- NOVA FUNÇÃO PARA DELETAR A REVIEW ---
    const handleDeleteReview = async (reviewId: number) => {
        // Guarda o estado original para o caso de erro
        const originalReviews = evento?.reviews || [];

        // Atualização otimista: remove a review da UI imediatamente
        setEvento(prev => prev ? {
            ...prev,
            reviews: prev.reviews?.filter(r => r.id !== reviewId)
        } : null);

        try {
            // A rota DELETE é em /api/reviews/{id}, não é um sub-recurso de evento
            const response = await authFetch(`/proxy/api/reviews/${reviewId}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                throw new Error("Falha ao excluir a review.");
            }
            // Se a API funcionou, a UI já está correta.
        } catch (error) {
            alert(error instanceof Error ? error.message : "Erro ao excluir review.");
            // Rollback: se a API falhar, restaura a review na UI
            setEvento(prev => prev ? { ...prev, reviews: originalReviews } : null);
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
                                    <button
                                        onClick={handleInteresse}
                                        disabled={isLiking}
                                        className={`btn-like ${jaTemInteresse ? 'liked' : ''}`}
                                        // --- NOVOS EVENTOS DE MOUSE ---
                                        onMouseEnter={() => setIsHovering(true)}
                                        onMouseLeave={() => setIsHovering(false)}
                                    >
                                        {/* --- NOVA LÓGICA PARA O TEXTO DO BOTÃO --- */}
                                        {jaTemInteresse
                                            ? (isHovering ? '❌ Remover Interesse' : '❤️ Interesse Demonstrado')
                                            : '👍 Tenho Interesse'
                                        }
                                    </button>
                                )}
                            </span>
                        </div>
                        <div className="reviews-section">
                            <div className="reviews-header">
                                <h2>Reviews</h2>
                                {/* --- LÓGICA DO BOTÃO CORRIGIDA --- */}
                                {/* O botão só aparece se:
                                    1. O usuário é um participante
                                    2. Ele já demonstrou interesse no evento
                                    3. Ele ainda não fez uma review para este evento
                                */}
                                {user && user.accountType === 'participante' && jaTemInteresse && !usuarioJaFezReview && (
                                    <button onClick={() => setReviewModalOpen(true)} className="btn-add-review">
                                        Adicionar Review
                                    </button>
                                )}
                            </div>

                            {evento.reviews && evento.reviews.length > 0 ? (
                                <div className="reviews-list">
                                    {/* Mapeia as reviews, passando o usuário atual e a função de deletar */}
                                    {evento.reviews.map(review => (
                                        <ReviewItem
                                            key={review.id}
                                            review={review}
                                            currentUser={user}
                                            onDelete={handleDeleteReview}
                                        />
                                    ))}
                                </div>
                            ) : (
                                <p>Nenhuma review ainda.</p>
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
                                            className={`${star <= (hoveredNota || nota) ? (star <= nota ? 'star-filled' : 'star-hover') : 'star-empty'}`}
                                            onClick={() => setNota(star)}
                                            onMouseEnter={() => setHoveredNota(star)}
                                            onMouseLeave={() => setHoveredNota(null)}
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