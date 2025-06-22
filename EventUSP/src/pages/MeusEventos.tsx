import "./basicStyle.css"
import Navbar from "../components/NavBar.tsx";
import { useState, useEffect } from 'react'; // Importar useRef e useEffect
import "./MeusEventos.css"

function MeusEventos() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    // const [username, setUsername] = useState<string | null>(null);

    // Efeito para verificar o estado de login ao montar o componente
    useEffect(() => {
        const token = localStorage.getItem('token');
        const storedUsername = localStorage.getItem('username');
        if (token && storedUsername) {
            setIsLoggedIn(true);
            // setUsername(storedUsername);
        } else {
            setIsLoggedIn(false);
            // setUsername(null);
        }

    }, []); // Array de dependências vazio para executar apenas uma vez na montagem

    return (
        <>
            <div className="home-page">
                <Navbar />
                {isLoggedIn ? (
                    <div>
                        <div className="texto-inicial-meus-eventos">
                            {/* Mostra apenas o primeiro nome */}
                            <span>Meus Eventos</span>
                        </div>
                        <div className="meus-eventos-container">

                        </div>
                    </div>
                ) : (
                    <div className="texto-inicial-meus-eventos">
                        <span>Faça Login para ver seus eventos</span>
                    </div>
                )}
            </div>
        </>

    );
}

export default MeusEventos;