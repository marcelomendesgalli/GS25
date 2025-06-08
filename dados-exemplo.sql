-- Dados de Exemplo para Escola Clima Monitor
-- Execute estes comandos no console H2 ou PostgreSQL para popular o banco com dados de teste

-- Inserir escolas de exemplo
INSERT INTO escola (nome, cidade, estado, ativo, data_criacao) VALUES
('EMEF Prof. João Silva', 'São Paulo', 'SP', true, CURRENT_TIMESTAMP),
('EMEF Maria Santos', 'São Paulo', 'SP', true, CURRENT_TIMESTAMP),
('EMEF Carlos Drummond', 'São Paulo', 'SP', true, CURRENT_TIMESTAMP),
('EMEF Monteiro Lobato', 'São Paulo', 'SP', false, CURRENT_TIMESTAMP),
('EMEF Cecília Meireles', 'São Paulo', 'SP', true, CURRENT_TIMESTAMP);

-- Inserir sensores de exemplo
INSERT INTO sensor (id_escola, localizacao, ativo, tipo, descricao, data_criacao) VALUES
(1, 'Sala 1A', true, 'temperatura', 'Sensor principal da sala 1A', CURRENT_TIMESTAMP),
(1, 'Sala 2B', true, 'temperatura', 'Sensor da sala 2B', CURRENT_TIMESTAMP),
(1, 'Pátio', true, 'temperatura', 'Sensor do pátio coberto', CURRENT_TIMESTAMP),
(2, 'Sala 3C', true, 'temperatura', 'Sensor da sala 3C', CURRENT_TIMESTAMP),
(2, 'Biblioteca', true, 'temperatura', 'Sensor da biblioteca', CURRENT_TIMESTAMP),
(3, 'Sala 4D', true, 'temperatura', 'Sensor da sala 4D', CURRENT_TIMESTAMP),
(3, 'Quadra', false, 'temperatura', 'Sensor da quadra (manutenção)', CURRENT_TIMESTAMP),
(5, 'Sala 5E', true, 'temperatura', 'Sensor da sala 5E', CURRENT_TIMESTAMP);

-- Inserir leituras de exemplo (últimas 24 horas)
INSERT INTO leitura (id_sensor, temperatura, umidade, timestamp) VALUES
-- Sensor 1 (Sala 1A)
(1, 26.5, 65.2, DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
(1, 27.2, 63.8, DATEADD('HOUR', -2, CURRENT_TIMESTAMP)),
(1, 28.1, 62.5, DATEADD('HOUR', -3, CURRENT_TIMESTAMP)),
(1, 29.3, 61.2, DATEADD('HOUR', -4, CURRENT_TIMESTAMP)),
(1, 30.5, 59.8, DATEADD('HOUR', -5, CURRENT_TIMESTAMP)),

-- Sensor 2 (Sala 2B)
(2, 25.8, 67.1, DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
(2, 26.4, 66.3, DATEADD('HOUR', -2, CURRENT_TIMESTAMP)),
(2, 27.9, 64.7, DATEADD('HOUR', -3, CURRENT_TIMESTAMP)),
(2, 28.7, 63.2, DATEADD('HOUR', -4, CURRENT_TIMESTAMP)),
(2, 29.8, 61.9, DATEADD('HOUR', -5, CURRENT_TIMESTAMP)),

-- Sensor 3 (Pátio)
(3, 31.2, 58.5, DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
(3, 32.1, 57.2, DATEADD('HOUR', -2, CURRENT_TIMESTAMP)),
(3, 33.5, 55.8, DATEADD('HOUR', -3, CURRENT_TIMESTAMP)),
(3, 34.2, 54.3, DATEADD('HOUR', -4, CURRENT_TIMESTAMP)),
(3, 35.1, 53.1, DATEADD('HOUR', -5, CURRENT_TIMESTAMP)),

-- Sensor 4 (Sala 3C)
(4, 27.3, 64.8, DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
(4, 28.1, 63.5, DATEADD('HOUR', -2, CURRENT_TIMESTAMP)),
(4, 29.2, 62.1, DATEADD('HOUR', -3, CURRENT_TIMESTAMP)),
(4, 30.1, 60.7, DATEADD('HOUR', -4, CURRENT_TIMESTAMP)),
(4, 31.3, 59.2, DATEADD('HOUR', -5, CURRENT_TIMESTAMP)),

-- Sensor 5 (Biblioteca)
(5, 24.5, 68.9, DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
(5, 25.2, 67.6, DATEADD('HOUR', -2, CURRENT_TIMESTAMP)),
(5, 26.1, 66.2, DATEADD('HOUR', -3, CURRENT_TIMESTAMP)),
(5, 26.8, 65.1, DATEADD('HOUR', -4, CURRENT_TIMESTAMP)),
(5, 27.5, 63.8, DATEADD('HOUR', -5, CURRENT_TIMESTAMP));

-- Inserir alguns alertas de exemplo
INSERT INTO alerta (id_leitura, tipo, mensagem, nivel, status, timestamp) VALUES
(13, 'Calor Extremo', 'Temperatura crítica detectada no pátio da EMEF Prof. João Silva. Risco extremo para a saúde dos estudantes. Recomenda-se suspender atividades ao ar livre e garantir hidratação adequada.', 'Crítico', 'Emitido', DATEADD('HOUR', -3, CURRENT_TIMESTAMP)),
(14, 'Calor Extremo', 'Temperatura crítica mantida no pátio. Situação requer atenção imediata da coordenação.', 'Crítico', 'Em Andamento', DATEADD('HOUR', -4, CURRENT_TIMESTAMP)),
(15, 'Calor Extremo', 'Temperatura extremamente alta no pátio. Medidas de emergência devem ser implementadas.', 'Crítico', 'Resolvido', DATEADD('HOUR', -5, CURRENT_TIMESTAMP)),
(5, 'Temperatura Elevada', 'Temperatura acima do confortável na Sala 1A. Monitoramento recomendado.', 'Médio', 'Visualizado', DATEADD('HOUR', -5, CURRENT_TIMESTAMP)),
(20, 'Temperatura Elevada', 'Temperatura elevada na Sala 3C. Verificar ventilação do ambiente.', 'Médio', 'Emitido', DATEADD('HOUR', -5, CURRENT_TIMESTAMP));

-- Inserir usuários de exemplo (para testes de autenticação local)
INSERT INTO usuario (email, nome, provider, provider_id, role, ativo, data_criacao) VALUES
('admin@escolaclima.com', 'Administrador Sistema', 'local', 'admin', 'ADMIN', true, CURRENT_TIMESTAMP),
('gestor@escola1.edu.br', 'João Gestor', 'local', 'gestor', 'GESTOR', true, CURRENT_TIMESTAMP),
('professor@escola1.edu.br', 'Maria Professora', 'local', 'user', 'USUARIO', true, CURRENT_TIMESTAMP),
('coordenador@escola2.edu.br', 'Carlos Coordenador', 'google', 'google_123', 'GESTOR', true, CURRENT_TIMESTAMP);

-- Verificar dados inseridos
SELECT 'Escolas inseridas:' as info, COUNT(*) as total FROM escola;
SELECT 'Sensores inseridos:' as info, COUNT(*) as total FROM sensor;
SELECT 'Leituras inseridas:' as info, COUNT(*) as total FROM leitura;
SELECT 'Alertas inseridos:' as info, COUNT(*) as total FROM alerta;
SELECT 'Usuários inseridos:' as info, COUNT(*) as total FROM usuario;

-- Consultas úteis para verificar os dados
SELECT 
    e.nome as escola,
    s.localizacao as sensor,
    l.temperatura,
    l.umidade,
    l.timestamp
FROM leitura l
JOIN sensor s ON l.id_sensor = s.id
JOIN escola e ON s.id_escola = e.id
ORDER BY l.timestamp DESC
LIMIT 10;

SELECT 
    a.tipo,
    a.nivel,
    a.status,
    e.nome as escola,
    s.localizacao as local,
    a.timestamp
FROM alerta a
JOIN leitura l ON a.id_leitura = l.id
JOIN sensor s ON l.id_sensor = s.id
JOIN escola e ON s.id_escola = e.id
ORDER BY a.timestamp DESC;

