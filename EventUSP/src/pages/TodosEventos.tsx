import { useState, useEffect } from "react";
import "react-responsive-carousel/lib/styles/carousel.min.css";
import { Carousel } from 'react-responsive-carousel';
import "./basicStyle.css";
import "./Home.css"; // Pode reutilizar o mesmo CSS da Home
import Navbar from "../components/NavBar.tsx";
import {Link} from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { CATEGORIAS_EVENTOS } from "../constants/Categorias.tsx";

// As interfaces podem ser as mesmas da Home
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

function TodosEventos() {
    const [eventos, setEventos] = useState<Evento[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [categoriaFiltro, setCategoriaFiltro] = useState<string>("Todos");
    const { user } = useAuth();

    useEffect(() => {
        const fetchEventos = async () => {
            try {
                // ALTERAÇÃO PRINCIPAL: Remove o filtro '?periodo=futuros' para buscar TODOS os eventos
                const response = await fetch("/proxy/api/eventos");
                if (!response.ok) {
                    throw new Error('Erro ao buscar eventos');
                }
                const eventosData: Evento[] = await response.json();

                const eventosCompletos = await Promise.all(
                    eventosData.map(async (evento) => {
                        try {
                            const imagensResponse = await fetch(`/proxy/api/eventos/${evento.id}/imagens`);
                            if (!imagensResponse.ok) {
                                console.warn(`Não foi possível buscar imagens para o evento ID: ${evento.id}`);
                                return { ...evento, imagens: [] };
                            }
                            const imagensData: ImagemEvento[] = await imagensResponse.json();
                            imagensData.sort((a, b) => a.ordem - b.ordem);

                            return { ...evento, imagens: imagensData };
                        } catch (imgError) {
                            console.error(`Erro ao processar imagens para o evento ID: ${evento.id}`, imgError);
                            return { ...evento, imagens: [] };
                        }
                    })
                );

                setEventos(eventosCompletos);
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

        fetchEventos();
    }, []);

    const eventosFiltrados = eventos.filter(evento => {
        if (categoriaFiltro === "Todos") {
            return true;
        }
        return evento.categoria === categoriaFiltro;
    });

    return (
        <>
            <div className="home-page">
                <Navbar />
                {/* Título da página atualizado */}
                <h1>Todos os Eventos</h1>
                <div className="filtro-categoria-container">
                    <label htmlFor="categoria-filtro">Filtrar por Categoria:</label>
                    <select
                        id="categoria-filtro"
                        value={categoriaFiltro}
                        onChange={(e) => setCategoriaFiltro(e.target.value)}
                        className="categoria-select-filtro"
                    >
                        <option value="Todos">Todos</option>
                        {CATEGORIAS_EVENTOS.map(cat => (
                            <option key={cat} value={cat}>{cat}</option>
                        ))}
                    </select>
                </div>
                {loading && <p>Carregando eventos...</p>}
                {error && <p>Erro ao carregar eventos: {error}</p>}
                {!loading && !error && (
                    <div className="lista-eventos">
                        {eventosFiltrados.length > 0 ? (
                            eventosFiltrados.map((evento) => (
                                <Link to={`/evento/${evento.id}`} key={evento.id} className="card-evento-link-home">
                                    <div key={evento.id} className="card-evento-home">
                                        {evento.imagens && evento.imagens.length > 0 ? (
                                            <Carousel showThumbs={false} infiniteLoop useKeyboardArrows autoPlay showStatus={false}>
                                                {evento.imagens.map((imagem) => (
                                                    <div key={imagem.id}>
                                                        <img src={imagem.url} alt={imagem.descricao || evento.titulo} />
                                                    </div>
                                                ))}
                                            </Carousel>
                                        ) : (
                                            <div className="placeholder-imagem">
                                                <span>Sem imagem para este evento</span>
                                            </div>
                                        )}
                                        <div className="card-evento-body-home">
                                            <h2>{evento.titulo}</h2>
                                            <p><strong>Data:</strong> {new Date(evento.dataHora).toLocaleString()}</p>
                                            <p><strong>Likes:</strong> {evento.numeroLikes ? (
                                                <span className="likesEvento">{evento.numeroLikes}</span>
                                            ): (<span className="likesEvento">0</span>)}
                                            </p>
                                        </div>
                                    </div>
                                </Link>
                            ))
                        ) : (
                            // Mensagem de fallback atualizada
                            <p>Nenhum evento encontrado para o filtro aplicado :(</p>
                        )}
                    </div>
                )}
            </div>
            {user?.accountType === 'organizador' && (
                <div className="fab-container">
                    <Link to="/criar-evento" className="fab">+</Link>
                </div>
            )}
        </>
    );
}

export default TodosEventos;