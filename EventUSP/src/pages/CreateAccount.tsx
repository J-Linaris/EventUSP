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
import "./CreateAccount.css";
import {Link} from 'react-router-dom';

// Componente de upload (simples)
function UploadFoto() {
    return (
        <div className="upload-foto">
            <label htmlFor="fotoPerfil">Foto de perfil:</label>
            <input type="file" id="fotoPerfil" accept="image/*" />
        </div>
    );
}

function CreateAccount() {
    const [email, setEmail] = useState('');
    const [user, setUser] = useState('');
    const [senha, setSenha] = useState('');
    const [tipoConta, setTipoConta] = useState<'organizador' | 'participante' | ''>('');

    return (
        <div className="create-account-page">
            <div id="create-account-square">
                <div className="create-account-header">
                    <span id="texto-bem-vindo">Crie sua conta Event<span id="span-USP-eventusp">USP</span></span>
                </div>
                <div className="form-holder">
                    <div className="form-fields">

                        <div className="tipo-conta-selector">
                            <label>
                                <input
                                    type="radio"
                                    name="tipoConta"
                                    value="organizador"
                                    checked={tipoConta === 'organizador'}
                                    onChange={() => setTipoConta('organizador')}
                                />
                                <span>Organizador</span>
                            </label>
                            <label>
                                <input
                                    type="radio"
                                    name="tipoConta"
                                    value="participante"
                                    checked={tipoConta === 'participante'}
                                    onChange={() => setTipoConta('participante')}
                                />
                                <span>Participante</span>
                            </label>
                        </div>

                        <input
                            type="email"
                            placeholder="Insira seu E-mail"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                        <input
                            type="user"
                            placeholder="Insira seu nome de usuário"
                            value={user}
                            onChange={(e) => setUser(e.target.value)}
                            required
                        />
                        <input
                            type="password"
                            placeholder="Insira sua senha"
                            value={senha}
                            onChange={(e) => setSenha(e.target.value)}
                            required
                        />

                        {tipoConta === 'organizador' && <UploadFoto />}
                    </div>
                    <div className="form-footer">
                        <button className="form-footer-button" type="submit">Criar conta</button>
                    <Link to="/login">
                        <button className="form-footer-button">Login</button>
                    </Link>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default CreateAccount;