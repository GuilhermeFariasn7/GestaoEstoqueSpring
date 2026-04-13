/**
 * MÓDULO PRINCIPAL - SISTEMA DE GESTÃO
 * Versão melhorada com organização, tratamento de erros e performance
 */

// ============================================================================
// CONFIGURAÇÕES GLOBAIS
// ============================================================================
const CONFIG = {
    MENSAGEM: {
        TEMPO_VISIVEL: 2000,
        TEMPO_FADE: 200
    },
    MASCARA: {
        TELEFONE: '(00) 00000-0000',
        CEP: '00000-000',
        CPF: '000.000.000-00',
        CNPJ: '00.000.000/0000-00'
    },
    SELETORES: {
        EXCLUIR: '.btn-excluir',
        PREVIEW: '#preview',
        EMPRESA: '#empresa',
        FILIAL: '#filial',
        FONE: '#fone',
        CEP: '#cep',
        ESTADO: '#estado',
        CIDADE: '#cidade',
        MENU_TOGGLE: '.menu-toggle',
        NAV_LINKS: '.nav-links',
        TAB_BUTTONS: '#estoqueTab button',
        CPF: '#cpf',
        CNPJ: '#cnpj',
        MENSAGEM: '.mensagem'
    }
};

// ============================================================================
// UTILITÁRIOS
// ============================================================================

/**
 * Utilitário para logging estruturado
 */
const Logger = {
    error: (mensagem, erro) => console.error(` ${mensagem}:`, erro),
    info: (mensagem) => console.log(`${mensagem}`),
    warn: (mensagem) => console.warn(` ${mensagem}`),
    success: (mensagem) => console.log(` ${mensagem}`)
};

/**
 * Utilitário para validações
 */
const Validator = {
    cep: (cep) => /^\d{8}$/.test(cep),
    telefone: (tel) => /^\d{10,11}$/.test(tel),
    email: (email) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)
};

// ============================================================================
// MÓDULO DE EXCLUSÃO
// ============================================================================
const ExclusaoModule = (function() {
    function init() {
        document.querySelectorAll(CONFIG.SELETORES.EXCLUIR).forEach(btn => {
            btn.addEventListener('click', handleExclusao);
        });
    }

    function handleExclusao(event) {
        event.stopPropagation();
        event.preventDefault();

        const clienteId = this.dataset.id;
        const clienteNome = this.dataset.nome || 'este cliente';

        if (!clienteId) {
            Logger.error('ID do cliente não encontrado');
            return;
        }

        // Modal de confirmação mais amigável (fallback para confirm)
        if (confirm(`Tem certeza que deseja excluir ${clienteNome}?`)) {
            window.location.href = `/excluirCliente/${clienteId}`;
        }
    }

    return { init };
})();

// ============================================================================
// MÓDULO DE PREVIEW DE IMAGEM
// ============================================================================
const PreviewModule = (function() {
    function init() {
        // Se houver input de imagem, configurar preview
        const fileInput = document.querySelector('input[type="file"]');
        if (fileInput) {
            fileInput.addEventListener('change', previewImagem);
        }
    }

    window.previewImagem = function(event) {
        const input = event.target;
        const preview = document.getElementById('preview');

        if (!preview) {
            Logger.warn('Elemento de preview não encontrado');
            return;
        }

        if (input.files && input.files[0]) {
            const file = input.files[0];

            // Validar tipo de arquivo
            if (!file.type.startsWith('image/')) {
                alert('Por favor, selecione uma imagem válida');
                return;
            }

            // Validar tamanho (máx 5MB)
            if (file.size > 5 * 1024 * 1024) {
                alert('A imagem deve ter no máximo 5MB');
                return;
            }

            const reader = new FileReader();

            reader.onload = e => {
                preview.src = e.target.result;
                preview.style.display = 'block';
                Logger.success('Imagem carregada com sucesso');
            };

            reader.onerror = () => {
                alert('Erro ao carregar a imagem');
                preview.style.display = 'none';
            };

            reader.readAsDataURL(file);
        } else {
            preview.src = '#';
            preview.style.display = 'none';
        }
    };

    return { init };
})();

// ============================================================================
// MÓDULO DE FILIAIS
// ============================================================================
const FilialModule = (function() {
    let abortController = null;

    function init() {
        const empresaSelect = document.querySelector(CONFIG.SELETORES.EMPRESA);
        if (empresaSelect) {
            empresaSelect.addEventListener('change', carregarFiliais);
        }
        if (empresaSelect.value) {
            carregarFiliais();
        }
    }

    window.carregarFiliais = async function() {
        const empresaSelect = document.getElementById("empresa");
        const empresaId = document.getElementById("empresa").value;
        const filialSelect = document.getElementById("filial");

        // Guarda o ID da filial que estava selecionada anteriormente (do DTO)
        const filialSelecionadaAnteriormente = filialSelect.getAttribute('data-selected') || filialSelect.value;

        // Reset select
        filialSelect.innerHTML = '<option value="">Selecione a filial</option>';
        filialSelect.disabled = true;

        if (!empresaId) {
            filialSelect.disabled = false;
            return;
        }

        // Cancelar requisição anterior se existir
        if (abortController) {
            abortController.abort();
        }

        abortController = new AbortController();

        try {
            filialSelect.innerHTML = '<option value="">Carregando...</option>';

            const response = await fetch(`/filiais/por-empresa/${empresaId}`, {
                signal: abortController.signal
            });

            if (!response.ok) throw new Error('Erro na requisição');

            const filiais = await response.json();

            filialSelect.innerHTML = '<option value="">Selecione a filial</option>';

            if (filiais.length === 0) {
                filialSelect.innerHTML = '<option value="">Nenhuma filial encontrada</option>';
            } else {
                filiais.forEach(filial => {
                    const opt = document.createElement("option");
                    opt.value = filial.idfilial;
                    opt.textContent = `${filial.idfilial} - ${filial.nome}`;

                    // Se o ID da filial corresponder ao que estava salvo no DTO, seleciona ele.
                    if (filialSelecionadaAnteriormente && filial.idfilial == filialSelecionadaAnteriormente) {
                        opt.selected = true;
                    }

                    filialSelect.appendChild(opt);
                });
            }
        } catch (error) {
            if (error.name === 'AbortError') {
                Logger.info('Requisição cancelada');
            } else {
                Logger.error('Erro ao carregar filiais', error);
                filialSelect.innerHTML = '<option value="">Erro ao carregar</option>';
            }
        } finally {
            filialSelect.disabled = false;
            abortController = null;
        }
    };

    return { init };
})();

// ============================================================================
// MÓDULO DE PRODUTO (Preview, Validações, etc)
// ============================================================================
const ProdutoModule = (function() {
    function init() {
        initPreviewImagem();
        initValidacoesEstoque();
        initCalculoMargem();
    }

    function initPreviewImagem() {
        const inputFoto = document.getElementById('inputFoto');
        const preview = document.getElementById('previewImagem');

        if (inputFoto && preview) {
            inputFoto.addEventListener('change', function(event) {
                const file = event.target.files[0];

                if (file) {
                    // Validar tipo
                    if (!file.type.startsWith('image/')) {
                        alert('Por favor, selecione uma imagem válida (JPG, PNG)');
                        this.value = '';
                        return;
                    }

                    // Validar tamanho (5MB)
                    if (file.size > 5 * 1024 * 1024) {
                        alert('A imagem deve ter no máximo 5MB');
                        this.value = '';
                        return;
                    }

                    const reader = new FileReader();
                    reader.onload = function(e) {
                        preview.src = e.target.result;
                        Logger.success('Imagem carregada com sucesso');
                    };
                    reader.readAsDataURL(file);
                }
            });
        }
    }

    function initValidacoesEstoque() {
        const estoqueMin = document.getElementById('estoqueMinimo');
        const estoqueMax = document.getElementById('estoqueMaximo');
        const estoqueAtual = document.getElementById('estoqueAtual');

        if (estoqueMin && estoqueMax) {
            estoqueMin.addEventListener('change', function() {
                const min = parseFloat(this.value) || 0;
                const max = parseFloat(estoqueMax.value) || 0;

                if (min > max && max > 0) {
                    alert('Estoque mínimo não pode ser maior que o máximo');
                    this.value = '';
                }
            });

            estoqueMax.addEventListener('change', function() {
                const max = parseFloat(this.value) || 0;
                const min = parseFloat(estoqueMin.value) || 0;

                if (max < min && min > 0) {
                    alert('Estoque máximo não pode ser menor que o mínimo');
                    this.value = '';
                }
            });
        }

        if (estoqueAtual && estoqueMin && estoqueMax) {
            estoqueAtual.addEventListener('change', function() {
                const atual = parseFloat(this.value) || 0;
                const min = parseFloat(estoqueMin.value) || 0;
                const max = parseFloat(estoqueMax.value) || 0;

                if (max > 0 && atual > max) {
                    Logger.warn(`Estoque atual (${atual}) acima do máximo (${max})`);
                }
                if (min > 0 && atual < min) {
                    Logger.warn(`Estoque atual (${atual}) abaixo do mínimo (${min})`);
                }
            });
        }
    }

    function initCalculoMargem() {
        const precoCusto = document.getElementById('precoCusto');
        const precoVenda = document.getElementById('precoVenda');

        if (precoCusto && precoVenda) {
            precoVenda.addEventListener('input', function() {
                const custo = parseFloat(precoCusto.value) || 0;
                const venda = parseFloat(this.value) || 0;

                if (custo > 0 && venda > 0 && venda < custo) {
                    this.style.borderColor = '#dc3545';
                    this.style.backgroundColor = '#fff8f8';
                    Logger.warn('Preço de venda menor que o custo!');
                } else {
                    this.style.borderColor = '';
                    this.style.backgroundColor = '';

                    if (custo > 0 && venda > 0) {
                        const margem = ((venda - custo) / venda * 100).toFixed(1);
                        Logger.info(`Margem de lucro: ${margem}%`);
                    }
                }
            });
        }
    }

    return { init };
})();



// ============================================================================
// MÓDULO DE MÁSCARAS
// ============================================================================
const MascaraModule = (function() {
    function init() {
        aplicarMascaras();
    }

    function aplicarMascaras() {
        // Telefone
        const telefone = document.querySelector(CONFIG.SELETORES.FONE);
        if (telefone && window.IMask) {
            IMask(telefone, {
                mask: CONFIG.MASCARA.TELEFONE,
                lazy: false
            });
        }

        // CEP
        const cep = document.querySelector(CONFIG.SELETORES.CEP);
        if (cep && window.IMask) {
            IMask(cep, {
                mask: CONFIG.MASCARA.CEP,
                lazy: false
            });
        }

        // CPF
        const cpf = document.querySelector(CONFIG.SELETORES.CPF);
        if (cpf && window.IMask) {
            IMask(cpf, {
                mask: CONFIG.MASCARA.CPF,
                lazy: false
            });
        }

        // CNPJ
        const cnpj = document.querySelector(CONFIG.SELETORES.CNPJ);
        if (cnpj && window.IMask) {
            IMask(cnpj, {
                mask: CONFIG.MASCARA.CNPJ,
                lazy: false
            });
        }
    }

    return { init };
})();

// ============================================================================
// MÓDULO DE ENDEREÇO (CEP + ESTADO/CIDADE)
// ============================================================================
const EnderecoModule = (function() {
    let cepTimeout = null;

    function init() {
        const estadoSelect = document.querySelector(CONFIG.SELETORES.ESTADO);
        const cidadeSelect = document.querySelector(CONFIG.SELETORES.CIDADE);
        const cepInput = document.querySelector(CONFIG.SELETORES.CEP);

        if (estadoSelect && cidadeSelect) {
            estadoSelect.addEventListener('change', handleEstadoChange);
        }

        if (cepInput) {
            cepInput.addEventListener('blur', handleCepBlur);
            cepInput.addEventListener('input', handleCepInput);
        }
    }

    function filtrarCidadesPorEstado(estadoId) {
        const cidadeSelect = document.querySelector(CONFIG.SELETORES.CIDADE);
        if (!cidadeSelect) return;

        Array.from(cidadeSelect.options).forEach(opt => {
            if (!opt.value) return;
            const estadoCidade = opt.dataset.estado;
            opt.style.display = estadoCidade === estadoId ? "block" : "none";
        });

        cidadeSelect.value = "";
    }

    function handleEstadoChange() {
        if (this.value) {
            filtrarCidadesPorEstado(this.value);
        }
    }

    function handleCepInput() {
        // Debounce para não fazer requisições a cada tecla
        if (cepTimeout) clearTimeout(cepTimeout);

        cepTimeout = setTimeout(() => {
            const cep = this.value.replace(/\D/g, '');
            if (cep.length === 8) {
                buscarCep(cep);
            }
        }, 500);
    }

    function handleCepBlur() {
        const cep = this.value.replace(/\D/g, '');
        if (cep.length === 8) {
            buscarCep(cep);
        }
    }

    async function buscarCep(cep) {
        if (!Validator.cep(cep)) {
            alert("CEP inválido");
            return;
        }

        const estadoSelect = document.querySelector(CONFIG.SELETORES.ESTADO);
        const cidadeSelect = document.querySelector(CONFIG.SELETORES.CIDADE);

        try {
            const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
            const data = await response.json();

            if (data.erro) {
                alert("CEP não encontrado");
                return;
            }

            // Preencher endereço se houver campos específicos
            preencherEndereco(data);

            // Selecionar estado e cidade
            if (estadoSelect) {
                estadoSelect.value = data.uf;
                filtrarCidadesPorEstado(data.uf);

                // Selecionar cidade correspondente
                Array.from(cidadeSelect.options).forEach(opt => {
                    if (opt.text.toLowerCase() === data.localidade.toLowerCase() &&
                        opt.dataset.estado === data.uf) {
                        opt.selected = true;
                    }
                });
            }

            Logger.success('CEP encontrado com sucesso');
        } catch (error) {
            Logger.error('Erro ao buscar CEP', error);
            alert("Erro ao buscar CEP");
        }
    }

    function preencherEndereco(data) {
        // Preencher campos adicionais se existirem
        const campos = {
            'logradouro': data.logradouro,
            'bairro': data.bairro,
            'complemento': data.complemento
        };

        Object.entries(campos).forEach(([campo, valor]) => {
            const input = document.querySelector(`[name="${campo}"]`);
            if (input && valor) {
                input.value = valor;
            }
        });
    }

    return { init };
})();

// ============================================================================
// MÓDULO DE INTERFACE (Menu, Tabs, etc)
// ============================================================================
const InterfaceModule = (function() {
    function init() {
        initMenuToggle();
        initTabs();
    }

    function initMenuToggle() {
        const btn = document.querySelector(CONFIG.SELETORES.MENU_TOGGLE);
        const menu = document.querySelector(CONFIG.SELETORES.NAV_LINKS);

        if (btn && menu) {
            btn.addEventListener("click", () => {
                menu.classList.toggle("active");
                btn.setAttribute('aria-expanded',
                    menu.classList.contains('active'));
            });
        }
    }

    function initTabs() {
        const triggerTabList = document.querySelectorAll(CONFIG.SELETORES.TAB_BUTTONS);

        if (triggerTabList.length > 0 && window.bootstrap) {
            triggerTabList.forEach(triggerEl => {
                triggerEl.addEventListener('click', event => {
                    event.preventDefault();
                    const tab = new bootstrap.Tab(triggerEl);
                    tab.show();
                });
            });
        }
    }

    return { init };
})();

// ============================================================================
// MÓDULO DE MENSAGENS
// ============================================================================
const MensagemModule = (function() {
    function init() {
        const mensagens = document.querySelectorAll(CONFIG.SELETORES.MENSAGEM);

        mensagens.forEach(msg => {
            setTimeout(() => fadeOutMensagem(msg), CONFIG.MENSAGEM.TEMPO_VISIVEL);
        });
    }

    function fadeOutMensagem(msg) {
        msg.classList.add('fade-out');

        setTimeout(() => {
            const isSucesso = msg.classList.contains('sucesso');
            const redirect = msg.dataset.redirect;

            msg.remove();

            if (isSucesso && redirect) {
                window.location.href = redirect.trim();
            }
        }, CONFIG.MENSAGEM.TEMPO_FADE);
    }

    return { init };
})();

// ============================================================================
// MÓDULO DE TOGGLE SENHA
// ============================================================================
const SenhaModule = (function() {
    function init() {
        toggleSenha("toggleSenha", "senha");
        toggleSenha("toggleConfSenha", "confirmaSenha");

        // Suporte para página de login
        toggleSenha("toggleSenhaLogin", "senha");
    }

    window.toggleSenha = function(botaoId, campoName) {
        const botao = document.getElementById(botaoId);
        const campo = document.querySelector(`input[name="${campoName}"]`);

        if (!botao || !campo) return;

        botao.addEventListener("click", function() {
            const tipo = campo.type === "password" ? "text" : "password";
            campo.type = tipo;

            const icone = this.querySelector('i');
            if (icone) {
                icone.classList.toggle("fa-eye");
                icone.classList.toggle("fa-eye-slash");
            }

            // Acessibilidade
            this.setAttribute('aria-label',
                tipo === 'password' ? 'Mostrar senha' : 'Esconder senha');
        });

        // Acessibilidade
        botao.setAttribute('role', 'button');
        botao.setAttribute('tabindex', '0');
        botao.setAttribute('aria-label', 'Mostrar senha');
    };

    return { init };
})();

// ============================================================================
// INICIALIZAÇÃO PRINCIPAL
// ============================================================================
document.addEventListener("DOMContentLoaded", function() {
    Logger.info('Inicializando sistema...');

    // Inicializar todos os módulos
    try {
        ExclusaoModule.init();
        PreviewModule.init();
        FilialModule.init();
        MascaraModule.init();
        EnderecoModule.init();
        InterfaceModule.init();
        MensagemModule.init();
        SenhaModule.init();
        ProdutoModule.init();

        Logger.success('Sistema inicializado com sucesso');
    } catch (error) {
        Logger.error('Erro ao inicializar sistema', error);
    }
});

// ============================================================================
// CLEANUP
// ============================================================================
window.addEventListener('beforeunload', function() {
    // Limpar timeouts e controllers se necessário
    if (window.cepTimeout) clearTimeout(window.cepTimeout);
});