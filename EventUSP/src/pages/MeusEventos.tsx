import "./basicStyle.css"
import Navbar from "../components/NavBar.tsx";
// import {
//     BrowserRouter,
//     Routes,
//     Route,
//     Link,
//     // NavLink,
//     // Navigate,
//     // Outlet
// } from 'react-router-dom';

function MeusEventos() {
    return (
        <>
            <div className="home-page">
                <Navbar />
                Meus Eventos
            </div>
        </>

    );
}

export default MeusEventos;