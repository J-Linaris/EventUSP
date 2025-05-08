import {Link} from 'react-router-dom';
import eventUSPLogo from "../assets/EventUSPLogoSemFundo.png";
import "./NavBar.css";

// import Home from "./pages/Home.tsx";
// import Login from "./pages/Login.tsx";
// import About from "./pages/About.tsx";

function Navbar() {
    return(
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
                <Link to="/login">
                    <button className="navbarButton">
                        Login
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
            </div>
        </nav>
    );
}

export default Navbar;