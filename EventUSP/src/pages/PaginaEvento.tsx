import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { Carousel } from 'react-responsive-carousel';
import "react-responsive-carousel/lib/styles/carousel.min.css";
import Navbar from "../components/NavBar.tsx";
import "./basicStyle.css";
import "./PaginaEvento.css"

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
}

function PaginaEvento() {
    const { id } = useParams<{ id: string }>();
    const [evento, setEvento] = useState<Evento | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

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
                        <p><strong>Organizador:</strong> <br/>
                            <div className="infosOrganizadorEvento">
                                <img src={evento.organizador.fotoPerfil} alt={`Foto de perfil de ${evento.organizador.nome}`} className="fotoPerfilOrganizador"/><span>{evento.organizador.nome}</span>
                            </div>
                        </p>
                        <p><strong>Likes:</strong> {evento.numeroLikes}</p>
                    </div>
                </div>
            </div>
        </>
    );
}

export default PaginaEvento;