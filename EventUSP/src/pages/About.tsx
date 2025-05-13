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

function About() {
    return (
        <>
            <div className="home-page">
                <Navbar />
                Sobre nós
            </div>
        </>
    );
}

export default About;