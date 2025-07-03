import { useState } from "react";
import { Link } from "react-router-dom";
import * as React from "react";
import "./Login.css";
import { useAuth } from "../context/AuthContext.tsx";


function Login() {
    const [email, setEmail] = useState("");
    const [senha, setSenha] = useState("");
    const [erro, setErro] = useState("");
    const auth = useAuth();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setErro("");

        try {
            const response = await fetch("/proxy/api/users/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    email: email,
                    password: senha, // <-- o backend espera "password"
                }),
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => null);
                const message = errorData?.message || "Erro ao fazer login.";
                throw new Error(message);
            }

            const data = await response.json();

            // A própria função login de auth cuidaŕa de armazenar no LocalStorage
            auth.login(data);
            // localStorage.setItem("token", data.token);
            // localStorage.setItem("username", data.user.nome);
            // localStorage.setItem("email", data.user.email);
            // localStorage.setItem("accountType", data.user.accountType); // opcional, se vier

            window.location.href = "/";
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
                    <span id="texto-bem-vindo">
                        Bem-vindo ao Event<span id="span-USP-eventusp">USP</span>!
                    </span>
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
                            <button className="form-footer-button" type="button">
                                Criar conta
                            </button>
                        </Link>
                        <button className="form-footer-button" type="submit">
                            Entrar
                        </button>
                    </div>
                    {erro && <div className="erro-login">{erro}</div>}
                </form>
            </div>
        </div>
    );
}

export default Login;
