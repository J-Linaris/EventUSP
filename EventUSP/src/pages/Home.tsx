import { useState, useEffect } from "react";
import "react-responsive-carousel/lib/styles/carousel.min.css"; // Importa os estilos do carrossel
import { Carousel } from 'react-responsive-carousel';
import "./basicStyle.css";
import "./Home.css";
import Navbar from "../components/NavBar.tsx";
import {Link} from "react-router-dom";
import { useAuth } from "../context/AuthContext"; // Importe o hook useAuth
import { CATEGORIAS_EVENTOS } from "../constants/Categorias.tsx"; // Importa as categorias

// Definindo a interface para o objeto de Imagem
interface ImagemEvento {
    id: number;
    url: string;
    descricao?: string;
    ordem: number;
}

// Definindo a interface para o objeto de Organizador
interface Organizador {
    id: number;
    nome: string;
    email: string;
}

// Definindo a interface para o objeto de Evento
interface Evento {
    id: number;
    titulo: string;
    descricao: string;
    dataHora: string;
    localizacao: string;
    categoria: string;
    organizador: Organizador;
    numeroLikes: number;
    imagens: ImagemEvento[]; // Adicionando a lista de imagens
}

function Home() {
    const [eventos, setEventos] = useState<Evento[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [categoriaFiltro, setCategoriaFiltro] = useState<string>("Todos"); // Estado para o filtro
    const { user } = useAuth(); // Pega o usuário logado do contexto

    useEffect(() => {
        const fetchEventos = async () => {
            try {
                // Rota para buscar os eventos futuros (sem as imagens ainda)
                const response = await fetch("/proxy/api/eventos?periodo=futuros");
                if (!response.ok) {
                    throw new Error('Erro ao buscar eventos');
                }
                const eventosData: Evento[] = await response.json();

                // Para cada evento, cria uma promessa para buscar suas imagens
                const eventosCompletos = await Promise.all(
                    eventosData.map(async (evento) => {
                        try {
                            // A rota para buscar as imagens do evento específico
                            const imagensResponse = await fetch(`/proxy/api/eventos/${evento.id}/imagens`);
                            if (!imagensResponse.ok) {
                                // Se a busca de imagens falhar para um evento, retorna o evento sem imagens
                                console.warn(`Não foi possível buscar imagens para o evento ID: ${evento.id}`);
                                return { ...evento, imagens: [] };
                            }
                            const imagensData: ImagemEvento[] = await imagensResponse.json();
                            imagensData.sort((a, b) => a.ordem - b.ordem); // Ordena as imagens

                            // Retorna o objeto do evento com a propriedade 'imagens' populada
                            return { ...evento, imagens: imagensData };
                        } catch (imgError) {
                            console.error(`Erro ao processar imagens para o evento ID: ${evento.id}`, imgError);
                            return { ...evento, imagens: [] }; // Retorna o evento sem imagens em caso de erro
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
    }, []); // O array vazio como segundo argumento garante que o useEffect será executado apenas uma vez

    // Filtra os eventos com base na categoria selecionada
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
                <h1>Próximos Eventos</h1>
                {/* DROPDOWN DE FILTRO DE CATEGORIA */}
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
                                    {/* Adiciona o carrossel de imagens se houver imagens */}
                                    {evento.imagens && evento.imagens.length > 0 ? (
                                        <Carousel showThumbs={false} infiniteLoop useKeyboardArrows autoPlay showStatus={false}>
                                            {evento.imagens.map((imagem) => (
                                                <div key={imagem.id}>
                                                    <img src={imagem.url} alt={imagem.descricao || evento.titulo} />
                                                </div>
                                            ))}
                                        </Carousel>
                                    ) : (
                                        // Placeholder para eventos sem imagem
                                        <div className="placeholder-imagem">
                                            <span>Sem imagem para este evento</span>
                                        </div>
                                    )}
                                    <div className="card-evento-body-home">
                                        <h2>{evento.titulo}</h2>
                                        {/*<p><strong>Descrição:</strong> {evento.descricao}</p>*/}
                                        <p><strong>Data:</strong> {new Date(evento.dataHora).toLocaleString()}</p>
                                        {/*<p><strong>Local:</strong> {evento.localizacao}</p>*/}
                                        {/*<p><strong>Categoria:</strong> {evento.categoria}</p>*/}
                                        {/*<p><strong>Organizador:</strong> {evento.organizador.nome}</p>*/}
                                        <p><strong>Likes:</strong> {evento.numeroLikes ? (
                                            <span className="likesEvento">{evento.numeroLikes}</span>
                                        ): (<span className="likesEvento">0</span>)}
                                        </p>
                                    </div>
                                </div>
                                </Link>
                            ))
                        ) : (
                            <p>Nenhum evento futuro encontrado para o filtro aplicado :(</p>
                        )}
                    </div>
                )}
            </div>
            {/* Agora a verificação é baseada no papel do usuário do contexto */}
            {user?.accountType === 'organizador' && (
                <div className="fab-container">
                    {/* REMOVIDO: O menu expansível foi totalmente removido */}
                    {/* <div className={`fab-menu ${isMenuOpen ? 'open' : ''}`}> ... </div> */}

                    {/* ALTERADO: O <button> foi substituído por um <Link> */}
                    {/* O 'onClick' foi removido e a propriedade 'to' foi adicionada */}
                    <Link to="/criar-evento" className="fab">+</Link>
                </div>
            )}
        </>
    );
}

export default Home;