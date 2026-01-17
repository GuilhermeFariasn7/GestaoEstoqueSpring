/* ===============================
   AÇÕES DE EXCLUSÃO
================================ */
document.querySelectorAll('.btn-excluir').forEach(btn => {
    btn.addEventListener('click', function (event) {
        event.stopPropagation();
        const clienteId = this.dataset.id;

        if (confirm("Tem certeza que deseja excluir este cliente?")) {
            window.location.href = "/excluirCliente/" + clienteId;
        }
    });
});

/* ===============================
   PREVIEW DE IMAGEM
================================ */
function previewImagem(event) {
    const input = event.target;
    const preview = document.getElementById('preview');

    if (input.files && input.files[0]) {
        const reader = new FileReader();

        reader.onload = e => {
            preview.src = e.target.result;
            preview.style.display = 'block';
        };

        reader.readAsDataURL(input.files[0]);
    } else {
        preview.src = '#';
        preview.style.display = 'none';
    }
}

/* ===============================
   FILIAIS POR EMPRESA
================================ */
function carregarFiliais() {
    const empresaId = document.getElementById("empresa").value;
    const filialSelect = document.getElementById("filial");

    filialSelect.innerHTML = '<option value="">Selecione a filial</option>';

    if (!empresaId) return;

    fetch(`/filiais/por-empresa/${empresaId}`)
        .then(res => res.json())
        .then(filiais => {
            filiais.forEach(filial => {
                const opt = document.createElement("option");
                opt.value = filial.idfilial;
                opt.textContent = `${filial.idfilial} - ${filial.nome}`;
                filialSelect.appendChild(opt);
            });
        })
        .catch(err => console.error("Erro ao carregar filiais:", err));
}

/* ===============================
   DOM READY
================================ */
document.addEventListener("DOMContentLoaded", function () {

    /* ===== ELEMENTOS ===== */
    const telefone = document.getElementById("fone");
    const cep = document.getElementById("cep");
    const estadoSelect = document.getElementById("estado");
    const cidadeSelect = document.getElementById("cidade");

    /* ===============================
       MÁSCARAS
    ================================ */
    if (telefone) {
        IMask(telefone, { mask: '(00) 00000-0000' });
    }

    if (cep) {
        IMask(cep, { mask: '00000-000' });
    }

    /* ===============================
       FILTRAR CIDADES POR ESTADO
    ================================ */
    function filtrarCidadesPorEstado(estadoId) {
        Array.from(cidadeSelect.options).forEach(opt => {
            if (!opt.value) return;

            const estadoCidade = opt.dataset.estado;
            opt.style.display = estadoCidade === estadoId ? "block" : "none";
        });

        cidadeSelect.value = "";
    }

    if (estadoSelect) {
        estadoSelect.addEventListener("change", function () {
            if (this.value) {
                filtrarCidadesPorEstado(this.value);
            }
        });
    }

    /* ===============================
       BUSCA DE CEP (OPCIONAL)
    ================================ */
    if (cep) {
        cep.addEventListener("blur", function () {
            const cepLimpo = cep.value.replace(/\D/g, '');

            if (!cepLimpo) return; // CEP NÃO é obrigatório

            if (cepLimpo.length !== 8) {
                alert("CEP inválido");
                return;
            }

            fetch(`https://viacep.com.br/ws/${cepLimpo}/json/`)
                .then(res => res.json())
                .then(data => {
                    if (data.erro) {
                        alert("CEP não encontrado");
                        return;
                    }

                    // Seleciona estado
                    estadoSelect.value = data.uf;
                    filtrarCidadesPorEstado(data.uf);

                    // Seleciona cidade correspondente
                    Array.from(cidadeSelect.options).forEach(opt => {
                        if (
                            opt.text.toLowerCase() === data.localidade.toLowerCase() &&
                            opt.dataset.estado === data.uf
                        ) {
                            opt.selected = true;
                        }
                    });
                })
                .catch(() => alert("Erro ao buscar CEP"));
        });
    }

    /* ===============================
       MÁSCARA CPF / CNPJ DINÂMICA
    ================================ */
    const cpfInput = document.getElementById("cpf");
    const cnpjInput = document.getElementById("cnpj");

    if (cpfInput) {
        IMask(cpfInput, { mask: '000.000.000-00' });
    }

    if (cnpjInput) {
        IMask(cnpjInput, { mask: '00.000.000/0000-00' });
    }

    /* ===============================
       MENSAGENS DE SUCESSO / ERRO
    ================================ */
    const mensagens = document.querySelectorAll(".mensagem");
    const tempoVisivel = 2000;
    const tempoFade = 200;

    mensagens.forEach(msg => {
        const isSucesso = msg.classList.contains('sucesso');

        setTimeout(() => {
            msg.classList.add('fade-out');

            setTimeout(() => {
                msg.remove();

                if (isSucesso && msg.dataset.redirect) {
                    window.location.href = msg.dataset.redirect.trim();
                }
            }, tempoFade);

        }, tempoVisivel);
    });

    /* ===============================
       TOGGLE SENHA
    ================================ */
    function toggleSenha(botaoId, campoName) {
        const botao = document.getElementById(botaoId);
        const campo = document.querySelector(`input[name="${campoName}"]`);

        if (!botao || !campo) return;

        botao.addEventListener("click", function () {
            const tipo = campo.type === "password" ? "text" : "password";
            campo.type = tipo;

            this.classList.toggle("fa-eye");
            this.classList.toggle("fa-eye-slash");
        });
    }

    toggleSenha("toggleSenha", "senha");
    toggleSenha("toggleConfSenha", "confirmaSenha");

});
