INSERT INTO consultants (nome, email, especialidade)
SELECT 'Ana Silva', 'ana@ey.com', 'Java'
WHERE NOT EXISTS (
  SELECT 1 FROM consultants WHERE email = 'ana@ey.com'
);

INSERT INTO consultants (nome, email, especialidade)
SELECT 'Bruno Costa', 'bruno@ey.com', 'Dados'
WHERE NOT EXISTS (
  SELECT 1 FROM consultants WHERE email = 'bruno@ey.com'
);
