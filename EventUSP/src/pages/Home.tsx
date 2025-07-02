import { useState, useEffect } from "react";
import "./basicStyle.css";
import "./Home.css";
import Navbar from "../components/NavBar.tsx";

// Definindo a interface para o objeto de Evento, baseado no seu backend
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
}

function Home() {
    const [eventos, setEventos] = useState<Evento[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchEventos = async () => {
            try {
                // Rota para buscar os eventos futuros
                const response = await fetch("/proxy/api/eventos?periodo=futuros");
                if (!response.ok) {
                    throw new Error('Erro ao buscar eventos');
                }
                const data: Evento[] = await response.json();
                setEventos(data);
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

    return (
        <>
            <div className="home-page">
                <Navbar />
                <h1>Próximos Eventos</h1>
                {loading && <p>Carregando eventos...</p>}
                {error && <p>Erro ao carregar eventos: {error}</p>}
                {!loading && !error && (
                    <div className="lista-eventos">
                        {eventos.length > 0 ? (
                            eventos.map((evento) => (
                                <div key={evento.id} className="card-evento">
                                    <h2>{evento.titulo}</h2>
                                    <p><strong>Descrição:</strong> {evento.descricao}</p>
                                    <p><strong>Data:</strong> {new Date(evento.dataHora).toLocaleString()}</p>
                                    <p><strong>Local:</strong> {evento.localizacao}</p>
                                    <p><strong>Categoria:</strong> {evento.categoria}</p>
                                    <p><strong>Organizador:</strong> {evento.organizador.nome}</p>
                                    <p><strong>Likes:</strong> {evento.numeroLikes}</p>
                                </div>
                            ))
                        ) : (
                            <p>Nenhum evento futuro encontrado :(</p>
                        )}
                    </div>
                )}
            </div>
        </>
    );
}

export default Home;
