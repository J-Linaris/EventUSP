import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./CriarEvento.css"; // Usaremos um CSS dedicado

function CriarEvento() {
    // Estados para cada campo do formulário
    const [titulo, setTitulo] = useState("");
    const [descricao, setDescricao] = useState("");
    const [dataHora, setDataHora] = useState("");
    const [localizacao, setLocalizacao] = useState("");
    const [categoria, setCategoria] = useState("");

    // Estados para gerenciar as imagens
    const [imagens, setImagens] = useState<string[]>([]);
    const [imagemAtual, setImagemAtual] = useState("");

    // Estados para feedback e controle
    const [erro, setErro] = useState("");
    const [loading, setLoading] = useState(false);

    const { user } = useAuth();
    const navigate = useNavigate();

    // Função para adicionar uma URL de imagem à lista
    const handleAddImagem = () => {
        if (imagemAtual && !imagens.includes(imagemAtual)) {
            setImagens([...imagens, imagemAtual]);
            setImagemAtual(""); // Limpa o input após adicionar
        }
    };

    // Função para remover uma imagem da lista
    const handleRemoveImagem = (urlParaRemover: string) => {
        setImagens(imagens.filter(img => img !== urlParaRemover));
    };

    // Função para submeter o formulário
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setErro("");

        if (!user || user.accountType !== 'organizador') {
            setErro("Apenas organizadores podem criar eventos.");
            return;
        }

        setLoading(true);

        try {
            // 1. Criar o evento principal
            const eventoResponse = await fetch("/proxy/api/eventos", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    titulo,
                    descricao,
                    dataHora, // O backend espera um formato ISO 8601 (YYYY-MM-DDTHH:MM)
                    localizacao,
                    categoria,
                    organizadorId: user.id, // ID do organizador logado
                }),
            });

            if (!eventoResponse.ok) {
                const errorData = await eventoResponse.json().catch(() => null);
                throw new Error(errorData?.error || "Falha ao criar o evento.");
            }

            const eventoCriado = await eventoResponse.json();
            const eventoId = eventoCriado.id;

            // 2. Adicionar as imagens uma a uma
            if (imagens.length > 0) { //
                for (const url of imagens) {
                    // 'await' aqui garante que o laço vai esperar a conclusão de cada fetch
                    // antes de prosseguir para a próxima iteração.
                    await fetch(`/proxy/api/eventos/${eventoId}/imagens`, { //
                        method: "POST", //
                        headers: { "Content-Type": "application/json" }, //
                        body: JSON.stringify({ url: url, descricao: "Imagem do evento" }), //
                    });
                }
            }

            // 3. Redirecionar para a página do evento recém-criado
            navigate(`/evento/${eventoId}`);

        } catch (err: unknown) {
            if (err instanceof Error) {
                setErro(err.message);
            } else {
                setErro("Ocorreu um erro inesperado.");
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="criar-evento-page">
            <div id="criar-evento-square">
                <div className="criar-evento-header">
                    <span id="texto-principal">
                        Crie seu <span id="span-USP-eventusp">Evento</span>
                    </span>
                </div>
                <form onSubmit={handleSubmit} className="criar-evento-form">
                    <div className="form-fields-evento">
                        <input type="text" placeholder="Título do Evento" value={titulo} onChange={(e) => setTitulo(e.target.value)} required />
                        <textarea placeholder="Descrição detalhada do evento" value={descricao} onChange={(e) => setDescricao(e.target.value)} required />
                        <input type="datetime-local" value={dataHora} onChange={(e) => setDataHora(e.target.value)} required />
                        <input type="text" placeholder="Localização (ex: Auditório da Reitoria)" value={localizacao} onChange={(e) => setLocalizacao(e.target.value)} required />
                        <input type="text" placeholder="Categoria (ex: Palestra, Show, Esportivo)" value={categoria} onChange={(e) => setCategoria(e.target.value)} required />

                        {/* Seção para adicionar imagens */}
                        <div className="image-input-section">
                            <input type="url" placeholder="URL da Imagem" value={imagemAtual} onChange={(e) => setImagemAtual(e.target.value)} />
                            <button type="button" onClick={handleAddImagem} className="add-image-btn">Adicionar Imagem</button>
                        </div>

                        {/* Lista de imagens adicionadas */}
                        <div className="image-preview-list">
                            {imagens.map((url, index) => (
                                <div key={index} className="image-preview-item">
                                    <img src={url} alt={`Preview ${index + 1}`} />
                                    <span>{url.substring(0, 30)}...</span>
                                    <button type="button" onClick={() => handleRemoveImagem(url)} className="remove-image-btn">X</button>
                                </div>
                            ))}
                        </div>
                    </div>
                    <div className="form-footer-evento">
                        <button className="form-footer-button-evento" type="submit" disabled={loading}>
                            {loading ? "Criando..." : "Criar Evento"}
                        </button>
                    </div>
                    {erro && <div className="erro-form-evento">{erro}</div>}
                </form>
            </div>
        </div>
    );
}

export default CriarEvento;
