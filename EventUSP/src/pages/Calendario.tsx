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


function Calendario() {
    return (
        <>
            <div className="home-page">
                <Navbar />
                Página Calendário
            </div>
        </>

    );
}

export default Calendario;