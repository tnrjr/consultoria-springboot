-- Cria 2 consultores (sem definir ID; usa IDENTITY)
INSERT INTO consultants (nome, email, especialidade)
VALUES ('Ana Silva', 'ana@ey.com', 'Java')
ON CONFLICT (email) DO NOTHING;

INSERT INTO consultants (nome, email, especialidade)
VALUES ('Bruno Costa', 'bruno@ey.com', 'Dados');
ON CONFLICT (email) DO NOTHING;