import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import Navbar from "../components/NavBar.tsx"; // Verifique o caminho
import { useAuth } from '../context/AuthContext.tsx'; // Importe seu hook useAuth
import "./basicStyle.css";
import "./MeusEventos.css";

// Interface para o objeto Evento que esperamos da API
// (Idealmente, viria de um arquivo de tipos compartilhado)
interface Evento {
    id: number;
    titulo: string;
    dataHora: string;
    localizacao: string;
    organizador: {
        id: number;
    };
}


function MeusEventos() {
    // Usa o AuthContext como única fonte de verdade para o estado do usuário
    const { user, token } = useAuth();
    const [eventos, setEventos] = useState<Evento[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (user && token) {
            const fetchMeusEventos = async () => {
                setLoading(true);
                setError(null);

                try {
                    // LÓGICA ALTERADA A PARTIR DAQUI
                    if (user.accountType === 'organizador') {
                        // 1. Endpoint agora busca TODOS os eventos
                        const response = await fetch('/proxy/api/eventos', {
                            headers: {
                                'Authorization': `Bearer ${token}`
                            }
                        });

                        if (!response.ok) {
                            throw new Error(`Erro ao buscar a lista de eventos: ${response.statusText}`);
                        }

                        const todosEventos: Evento[] = await response.json();

                        // 2. Filtra a lista de eventos no frontend
                        const eventosDoOrganizador = todosEventos.filter(
                            (evento) => evento.organizador?.id === user.id
                        );

                        setEventos(eventosDoOrganizador);

                    } else {
                        // LÓGICA PARA PARTICIPANTE (AGORA FUNCIONA!)
                        const endpoint = `/proxy/api/participantes/${user.id}`;
                        const response = await fetch(endpoint, {
                            headers: { 'Authorization': `Bearer ${token}` }
                        });

                        if (!response.ok) {
                            throw new Error(`Erro ao buscar dados do participante: ${response.statusText}`);
                        }

                        const data = await response.json();

                        // O backend agora envia 'eventosInteressados' com os objetos completos!
                        setEventos(data.eventosInteressados || []);
                    }
                } catch (err) {
                    if (err instanceof Error) {
                        setError(err.message);
                    } else {
                        setError("Ocorreu um erro desconhecido.");
                    }
                } finally {
                    setLoading(false);
                }
            };

            fetchMeusEventos();
        } else {
            setEventos([]);
            setLoading(false);
        }
    }, [user, token]); // O useEffect re-executa se 'user' ou 'token' mudarem

    const renderContent = () => {
        if (loading) {
            return <p>Carregando seus eventos...</p>;
        }
        if (error) {
            return <p className="erro-mensagem">Erro ao carregar: {error}</p>;
        }
        if (!user) { //
            return <span>Faça Login para ver seus eventos.</span>;
        }

        // Renderização principal quando o usuário está logado
        return (
            <div>
                <div className="texto-inicial-meus-eventos">
                    <h1>
                        {user.accountType === 'organizador'
                            ? `Eventos organizados por ${user.nome}`
                            : `Eventos que ${user.nome} tem interesse`}
                    </h1>
                </div>
                <div className="meus-eventos-container">
                    {eventos.length > 0 ? (
                        eventos.map(evento => (
                            <Link to={`/evento/${evento.id}`} key={evento.id} className="evento-card">
                                <h3>{evento.titulo}</h3>
                                <p><strong>Data:</strong> {new Date(evento.dataHora).toLocaleDateString('pt-BR')}</p>
                                <p><strong>Local:</strong> {evento.localizacao}</p>
                            </Link>
                        ))
                    ) : (
                        <p>Nenhum evento encontrado.</p>
                    )}
                </div>
            </div>
        );
    };

    return (
        <div className="home-page">
            <Navbar />
            <div className="conteudo-principal">
                {renderContent()}
            </div>
        </div>
    );
}

export default MeusEventos;