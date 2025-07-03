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
import CreateAccount from "./pages/CreateAccount.tsx";
import PaginaEvento from './pages/PaginaEvento.tsx'; // Criaremos este componente a seguir
import { AuthProvider } from './context/AuthContext'; // Importe o Provider
import PaginaCriarEvento from './pages/CriarEvento.tsx'

function App() {

  return (
      <BrowserRouter>
          <AuthProvider>
              {/* Definição das rotas */}
              <Routes>
                  <Route path="/" element={<Home />} /> /* Você está definindo a rota aqui*/
                  <Route path="/about" element={<About />} />
                  <Route path="/login" element={<Login />} />
                  <Route path="/create-account" element={<CreateAccount />} />
                  <Route path="/meusEventos" element={<MeusEventos />} />
                  <Route path="/calendario" element={<Calendario />} />
                  <Route path="/evento/:id" element={<PaginaEvento />} />
                  <Route path="/criar-evento" element={<PaginaCriarEvento/>} />
              </Routes>
          </AuthProvider>
      </BrowserRouter>
  )
}

export default App
