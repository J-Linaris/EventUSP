import { createContext, useState, useContext, useEffect, ReactNode } from 'react';

// Interfaces para o usuário e o contexto
interface User {
    id: number;
    nome: string;
    email: string;
    accountType: 'organizador' | 'participante'; // Adicionamos a propriedade 'role'
}

// Interface para a resposta da API de login
interface LoginResponseData {
    user: {
        id: number;
        nome: string;
        email: string;
    };
    token: string;
    role: 'organizador' | 'participante'; // O tipo de conta vem diretamente da API
}

// Interface para o contexto de autorização
interface AuthContextType {
    user: User | null;
    token: string | null;
    // A função login agora espera o objeto de resposta da API
    login: (data: LoginResponseData) => void;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [user, setUser] = useState<User | null>(null);
    const [token, setToken] = useState<string | null>(null);

    useEffect(() => {
        // Tenta carregar dados do localStorage ao iniciar a aplicação
        const storedToken = localStorage.getItem('token');
        const storedUser = localStorage.getItem('user');
        if (storedToken && storedUser) {
            setToken(storedToken);
            setUser(JSON.parse(storedUser));
        }
    }, []);

    // Função 'login' atualizada
    const login = (data: LoginResponseData) => {
        // Mapeia diretamente os dados recebidos do backend
        const authenticatedUser: User = {
            id: data.user.id,
            nome: data.user.nome,
            email: data.user.email,
            accountType: data.role // Usa diretamente o 'role' retornado pela API
        };

        setUser(authenticatedUser);
        setToken(data.token);

        // Salva no localStorage
        localStorage.setItem('user', JSON.stringify(authenticatedUser));
        localStorage.setItem('token', data.token);
    };

    const logout = () => {
        setUser(null);
        setToken(null);
        // Limpa todos os dados de usuário do localStorage
        localStorage.removeItem('user');
        localStorage.removeItem('token');
    };

    return (
        <AuthContext.Provider value={{ user, token, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

// Hook customizado para facilitar o uso do contexto
export const useAuth = (): AuthContextType => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth deve ser usado dentro de um AuthProvider');
    }
    return context;
};
