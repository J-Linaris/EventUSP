// import {
//     BrowserRouter,
//     Routes,
//     Route,
//     Link,
//     // NavLink,
//     // Navigate,
//     // Outlet
// } from 'react-router-dom';
import { useState } from "react";
import "./basicStyle.css";
import "./Login.css";
import {Link} from 'react-router-dom';

function Login() {
    const [email, setEmail] = useState("");
    const [senha, setSenha] = useState("");
    const [erro, setErro] = useState("");

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setErro("");

        try {
            const response = await fetch("/proxy/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ email, senha }),
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || "Erro ao fazer login.");
            }

            const data = await response.json();
            // Exemplo: armazenar o token JWT no localStorage
            localStorage.setItem("token", data.token);

            // Redirecionar ou atualizar estado de login
            window.location.href = "/home"; // ajuste conforme seu app
        } catch (err: unknown) {
            if (err instanceof Error) {
                setErro(err.message);
            } else {
                setErro("Erro inesperado.");
            }
        }
    };

    return (
        <div className="login-page">
            <div id="login-square">
                    <div className="login-header">
                        <span id="texto-bem-vindo">Bem-vindo ao Event<span id="span-USP-eventusp">USP</span>!</span>
                    </div>
                    <form onSubmit={handleSubmit} className="login-form">
                        <div className="form-fields">
                            <input
                                type="email"
                                placeholder="Email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                            />
                            <input
                                type="password"
                                placeholder="Senha"
                                value={senha}
                                onChange={(e) => setSenha(e.target.value)}
                                required
                            />
                        </div>
                        <div className="form-footer">
                            <Link to="/create-account">
                                <button className="form-footer-button">Criar conta</button>
                            </Link>
                            <button className="form-footer-button" type="submit">Entrar</button>
                        </div>
                        {erro && <div className="erro-login">{erro}</div>}
                    </form>
            </div>
        </div>
    );
}

export default Login;