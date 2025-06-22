import { useState, useEffect, useRef } from 'react'; // Importar useRef e useEffect
import { Link, useNavigate } from 'react-router-dom'; // Importar useNavigate
import eventUSPLogo from "/src/imgs/EventUSPLogoSemFundo.png";
import './NavBar.css';
import genericProfileIcon from '/src/imgs/icon_profile_pic.png'; // Crie ou use um ícone de perfil genérico

function Navbar() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [username, setUsername] = useState<string | null>(null);
    const [showDropdown, setShowDropdown] = useState(false); // Estado para controlar a visibilidade do dropdown
    const navigate = useNavigate(); // Hook para navegação programática
    const dropdownRef = useRef<HTMLDivElement>(null); // Ref para fechar o dropdown ao clicar fora

    // Efeito para verificar o estado de login ao montar o componente
    useEffect(() => {
        const token = localStorage.getItem('token');
        const storedUsername = localStorage.getItem('username');
        if (token && storedUsername) {
            setIsLoggedIn(true);
            setUsername(storedUsername);
        } else {
            setIsLoggedIn(false);
            setUsername(null);
        }

        // Adicionar listener de clique para fechar o dropdown ao clicar fora
        const handleClickOutside = (event: MouseEvent) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
                setShowDropdown(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);

        // Limpeza do listener ao desmontar o componente
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []); // Array de dependências vazio para executar apenas uma vez na montagem

    // Função para fazer logout
    const handleLogout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        localStorage.removeItem('email');
        localStorage.removeItem('accountType');
        setIsLoggedIn(false); // Atualiza o estado de login
        setUsername(null); // Limpa o nome de usuário
        setShowDropdown(false); // Fecha o dropdown
        navigate('/'); // Redireciona para a página inicial ou login
    };

    // Função para alternar a visibilidade do dropdown
    const toggleDropdown = () => {
        setShowDropdown(prev => !prev);
    };

    return (
        <nav id="navBarComponent">
            {/* Container do logo à esquerda*/}
            <div className="flex" id="logoContainer">
                <Link to="/">
                    <img src={eventUSPLogo} id="logo" alt="Logo EventUSP" />
                </Link>
            </div>

            {/* Barra de pesquisa central */}
            <div className="flex">
                <input
                    type="text"
                    placeholder="Pesquisar evento"
                    className="searchBar"
                />
            </div>

            {/* Botões alinhados da direita para a esquerda */}
            <div className="navbarButtonsComponent">
                <Link to="/">
                    <button className="navbarButton">
                        Página Inicial
                    </button>
                </Link>
                <Link to="/meusEventos">
                    <button className="navbarButton">
                        Meus Eventos
                    </button>
                </Link>
                <Link to="/calendario">
                    <button className="navbarButton">
                        Calendário
                    </button>
                </Link>
                <Link to="/about">
                    <button className="navbarButton">
                        Sobre nós
                    </button>
                </Link>

                {/* Renderização condicional do botão de Login ou Perfil */}
                {isLoggedIn ? (
                    <div className="profile-dropdown-container" ref={dropdownRef}>
                        <button className="navbarButton profile-button" onClick={toggleDropdown}>
                            <img src={genericProfileIcon} alt="Perfil" className="profile-icon" />
                            {username && <span className="profile-username">{username.split(' ')[0]}</span>} {/* Mostra apenas o primeiro nome */}
                        </button>
                        {showDropdown && (
                            <div className="dropdown-menu">
                                {/* Você pode adicionar mais opções aqui se precisar */}
                                <button onClick={handleLogout} className="dropdown-item">
                                    Sair da conta
                                </button>
                            </div>
                        )}
                    </div>
                ) : (
                    <Link to="/login">
                        <button className="navbarButton">
                            Login
                        </button>
                    </Link>
                )}
            </div>
        </nav>
    );
}

export default Navbar;