import { useState } from "react";
import "./basicStyle.css";
import "./CreateAccount.css";
import { Link, useNavigate } from 'react-router-dom';
import * as React from "react";

function CreateAccount() {
    const [email, setEmail] = useState('');
    const [user, setUser] = useState('');
    const [senha, setSenha] = useState('');
    const [tipoConta, setTipoConta] = useState<'organizador' | 'participante' | ''>('');
    const [fotoPerfilUrl, setFotoPerfilUrl] = useState<string>('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(false);
    
    const navigate = useNavigate();

    // const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    //     if (event.target.files && event.target.files[0]) {
    //         setFotoPerfil(event.target.files[0]);
    //     }
    // };

    const validarFormulario = () => {
        if (!email || !user || !senha) {
            setError('Por favor, preencha todos os campos obrigatórios.');
            return false;
        }
        
        if (!tipoConta) {
            setError('Por favor, selecione o tipo de conta.');
            return false;
        }

        // Verifica se a URL foi preenchida para o organizador
        if (tipoConta === 'organizador' && !fotoPerfilUrl.trim()) {
            setError('Como organizador, você precisa fornecer uma URL para a foto de perfil.');
            return false;
        }

        // Validação básica de email
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            setError('Por favor, insira um email válido.');
            return false;
        }
        
        // Validação de senha (mínimo 6 caracteres)
        if (senha.length < 6) {
            setError('A senha deve ter pelo menos 6 caracteres.');
            return false;
        }
        
        return true;
    };

    // A função criarConta foi modificada para enviar os dados como JSON
    const criarConta = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setError('');

        if (!validarFormulario()) {
            return;
        }

        setLoading(true);

        try {
            // Prepara o corpo da requisição como um objeto JavaScript
            const requestBody = {
                email,
                username: user,
                password: senha,
                accountType: tipoConta,
                // Inclui a URL da foto apenas se for um organizador
                profilePhoto: tipoConta === 'organizador' ? fotoPerfilUrl : null,
            };

            // Envia os dados como JSON, definindo o cabeçalho Content-Type
            const response = await fetch('/proxy/api/users/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(requestBody), // Envia o objeto como uma string JSON
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Erro ao criar conta.');
            }

            const data = await response.json();

            if (data.token) {
                localStorage.setItem('authToken', data.token);
            }

            setSuccess(true);

            setTimeout(() => {
                navigate('/login');
            }, 2000);

        } catch (err: unknown) {
            if (err instanceof Error) {
                setError(err.message);
            } else {
                setError('Ocorreu um erro ao criar sua conta. Por favor, tente novamente.');
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="create-account-page">
            <div id="create-account-square">
                <div className="create-account-header">
                    <span id="texto-bem-vindo">Crie sua conta Event<span id="span-USP-eventusp">USP</span></span>
                </div>
                <div className="form-holder">
                    <form onSubmit={criarConta} className="form-fields">
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
                            type="text"
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
                        {/* Campo para inserir a URL da foto, visível apenas para organizadores */}
                        {tipoConta === 'organizador' && (
                            <input
                                type="url"
                                placeholder="URL da sua foto de perfil"
                                value={fotoPerfilUrl}
                                onChange={(e) => setFotoPerfilUrl(e.target.value)}
                                required
                            />
                        )}
                        
                        {error && <div className="error-message">{error}</div>}
                        {success && <div className="success-message">Conta criada com sucesso! Redirecionando...</div>}
                        
                        <div className="form-footer">
                            <div className="form-footer-button-wrapper">
                            <button 
                                className="form-footer-button" 
                                type="submit"
                                disabled={loading}
                            >
                                {loading ? 'Criando...' : 'Criar conta'}
                            </button>
                            </div>
                            <div className="form-footer-button-wrapper">
                            <Link to="/login" className="link-button-wrapper">
                                <button className="form-footer-button" type="button">Voltar ao Login</button>
                            </Link>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}

export default CreateAccount;