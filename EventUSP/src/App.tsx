import {
    BrowserRouter,
    Routes,
    Route,
    // Link,
    // NavLink,
    // Navigate,
    // Outlet
} from 'react-router-dom';
import './App.css';
import Home from "./pages/Home.tsx";
import About from "./pages/About.tsx";
import Login from "./pages/Login.tsx";
import MeusEventos from "./pages/MeusEventos.jsx";
import Calendario from "./pages/Calendario.tsx";

function App() {

  return (
      <BrowserRouter>
          {/* Definição das rotas */}
          <Routes>
              <Route path="/" element={<Home />} /> /* Você está definindo a rota aqui*/
              <Route path="/about" element={<About />} />
              <Route path="/login" element={<Login />} />
              <Route path="/meusEventos" element={<MeusEventos />} />
              <Route path="/calendario" element={<Calendario />} />
          </Routes>
      </BrowserRouter>
  )
}

export default App
