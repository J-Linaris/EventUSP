import "./basicStyle.css"
// import {
//     BrowserRouter,
//     Routes,
//     Route,
//     Link,
//     // NavLink,
//     // Navigate,
//     // Outlet
// } from 'react-router-dom';
import Navbar from "../components/NavBar.tsx";

function Home() {
    return (
        <>
           <div className="home-page">
            <Navbar />
               Página Inicial
           </div>
        </>
    );
}

export default Home;