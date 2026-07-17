INSERT INTO categoria_geral (nm_categoria, ds_icon)
VALUES
    ('Eletricista', 'Bolt'),
    ('Diarista', 'CleaningServices'),
    ('Encanador', 'Plumbing'),
    ('Jardineiro', 'Grass'),
    ('Pintor', 'FormatPaint'),
    ('Montador de Móveis', 'Straighten'),
    ('Pedreiro', 'Foundation'),
    ('Carpinteiro', 'Carpenter'),
    ('Marceneiro', 'Handyman'),
    ('Serralheiro', 'Construction'),
    ('Vidraceiro', 'Window'),
    ('Gesseiro', 'Architecture'),
    ('Azulejista', 'GridOn'),
    ('Chaveiro', 'VpnKey'),
    ('Mecânico', 'Build'),
    ('Borracheiro', 'TireRepair'),
    ('Técnico de Informática', 'Computer'),
    ('Técnico de Ar-Condicionado', 'AcUnit'),
    ('Técnico de Eletrodomésticos', 'HomeRepairService'),
    ('Instalador de Câmeras', 'Videocam'),
    ('Instalador de Internet', 'Router'),
    ('Instalador de Energia Solar', 'SolarPower'),
    ('Dedetizador', 'PestControl'),
    ('Piscineiro', 'Pool'),
    ('Faxineiro', 'CleaningServices'),
    ('Cuidador de Idosos', 'Elderly'),
    ('Babá', 'ChildCare'),
    ('Cozinheiro', 'Restaurant'),
    ('Confeiteiro', 'Cake'),
    ('Fotógrafo', 'PhotoCamera'),
    ('Designer Gráfico', 'DesignServices'),
    ('Costureiro', 'Checkroom'),
    ('Cabeleireiro', 'ContentCut'),
    ('Barbeiro', 'Face'),
    ('Manicure e Pedicure', 'Spa'),
    ('Personal Trainer', 'FitnessCenter'),
    ('Professor Particular', 'School'),
    ('Motorista', 'DirectionsCar'),
    ('Entregador', 'DeliveryDining'),
    ('Frete e Mudanças', 'LocalShipping');

INSERT INTO categoria_especifica (
    fk_id_categoria_geral,
    nm_categoria_especifica
)
VALUES
    -- Eletricista
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Eletricista'),
        'Instalação elétrica'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Eletricista'),
        'Manutenção elétrica'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Eletricista'),
        'Instalação de tomadas e interruptores'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Eletricista'),
        'Instalação de chuveiro'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Eletricista'),
        'Troca de fiação'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Eletricista'),
        'Instalação de disjuntores'
    ),

    -- Diarista
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Diarista'),
        'Limpeza residencial'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Diarista'),
        'Limpeza comercial'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Diarista'),
        'Limpeza pós-obra'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Diarista'),
        'Limpeza pesada'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Diarista'),
        'Organização de ambientes'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Diarista'),
        'Passadoria de roupas'
    ),

    -- Encanador
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Encanador'),
        'Reparo de vazamentos'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Encanador'),
        'Desentupimento'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Encanador'),
        'Instalação de torneiras'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Encanador'),
        'Instalação de vasos sanitários'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Encanador'),
        'Manutenção hidráulica'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Encanador'),
        'Instalação de caixa-d’água'
    ),

    -- Jardineiro
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Jardineiro'),
        'Manutenção de jardim'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Jardineiro'),
        'Poda de árvores'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Jardineiro'),
        'Corte de grama'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Jardineiro'),
        'Plantio de flores e plantas'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Jardineiro'),
        'Paisagismo'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Jardineiro'),
        'Limpeza de terrenos'
    ),

    -- Pintor
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Pintor'),
        'Pintura residencial'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Pintor'),
        'Pintura comercial'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Pintor'),
        'Pintura externa'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Pintor'),
        'Pintura de portas e janelas'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Pintor'),
        'Aplicação de textura'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Pintor'),
        'Aplicação de massa corrida'
    ),

    -- Montador de Móveis
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Montador de Móveis'),
        'Montagem de guarda-roupa'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Montador de Móveis'),
        'Montagem de móveis planejados'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Montador de Móveis'),
        'Montagem de móveis de escritório'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Montador de Móveis'),
        'Desmontagem de móveis'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Montador de Móveis'),
        'Instalação de prateleiras'
    ),

    -- Pedreiro
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Pedreiro'),
        'Construção de paredes'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Pedreiro'),
        'Reboco'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Pedreiro'),
        'Contrapiso'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Pedreiro'),
        'Pequenas reformas'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Pedreiro'),
        'Construção de churrasqueira'
    ),

    -- Técnico de Informática
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Técnico de Informática'),
        'Formatação de computadores'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Técnico de Informática'),
        'Manutenção de computadores'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Técnico de Informática'),
        'Instalação de programas'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Técnico de Informática'),
        'Remoção de vírus'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Técnico de Informática'),
        'Configuração de redes'
    ),

    -- Técnico de Ar-Condicionado
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Técnico de Ar-Condicionado'),
        'Instalação de ar-condicionado'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Técnico de Ar-Condicionado'),
        'Manutenção de ar-condicionado'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Técnico de Ar-Condicionado'),
        'Limpeza de ar-condicionado'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Técnico de Ar-Condicionado'),
        'Recarga de gás'
    ),

    -- Chaveiro
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Chaveiro'),
        'Abertura de portas'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Chaveiro'),
        'Cópia de chaves'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Chaveiro'),
        'Troca de fechaduras'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Chaveiro'),
        'Instalação de fechaduras digitais'
    ),

    -- Cuidador de Idosos
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Cuidador de Idosos'),
        'Acompanhamento domiciliar'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Cuidador de Idosos'),
        'Acompanhamento hospitalar'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Cuidador de Idosos'),
        'Auxílio em atividades diárias'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Cuidador de Idosos'),
        'Acompanhamento em consultas'
    ),

    -- Frete e Mudanças
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Frete e Mudanças'),
        'Pequenos fretes'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Frete e Mudanças'),
        'Mudança residencial'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Frete e Mudanças'),
        'Mudança comercial'
    ),
    (
        (SELECT id_categoria_geral FROM categoria_geral WHERE nm_categoria = 'Frete e Mudanças'),
        'Transporte de móveis'
    );