document.addEventListener("DOMContentLoaded", function() {
  const mensagens = document.querySelectorAll(".mensagem");
  const tempoVisivel = 2000;
  const tempoFade = 200;

  mensagens.forEach(msg => {
    const isSucesso = msg.classList.contains('sucesso');

    setTimeout(() => {
      msg.classList.add('fade-out');

      setTimeout(() => {
        msg.remove();

        if (isSucesso) {
          const rota = msg.dataset.redirect;
          if (rota) {
            window.location.href = rota.trim();
          }
        }

      }, tempoFade);

    }, tempoVisivel);
  });

  const toggleSenha = document.getElementById('toggleSenha');
  const campoSenha = document.querySelector('input[name="senha"]');

  if (toggleSenha && campoSenha) {
    toggleSenha.addEventListener('click', function() {
      const tipo = campoSenha.getAttribute('type') === 'password' ? 'text' : 'password';
      campoSenha.setAttribute('type', tipo);

      this.classList.toggle('fa-eye');
      this.classList.toggle('fa-eye-slash');
    });
  }

  const toggleConfSenha = document.getElementById('toggleConfSenha');
  const campoConfSenha = document.querySelector('input[name="confirmaSenha"]');

  if (toggleConfSenha && campoConfSenha) {
    toggleConfSenha.addEventListener('click', function() {
      const tipoC = campoConfSenha.getAttribute('type') === 'password' ? 'text' : 'password';
      campoConfSenha.setAttribute('type', tipoC);

      this.classList.toggle('fa-eye');
      this.classList.toggle('fa-eye-slash');
    });
  }

  // ====== Máscaras usando Inputmask ======
  if (typeof Inputmask === 'undefined') {
    console.error('Inputmask não carregado!');
    return;
  }

  // Campo CPF/CNPJ com máscara dinâmica
  const campoCpf = document.querySelector('input[name="cpf"]');
    if (campoCpf) {
      Inputmask({
        mask: ['999.999.999-99', '99.999.999/9999-99'],
        keepStatic: true,
        clearIncomplete: true,
        autoUnmask: false,
        onincomplete: function() {
          // opcional: pode mostrar erro ou algo assim
        }
      }).mask(campoCpf);
    }

  const campoTelefone = document.querySelector('input[name="telefone"]');
  if (campoTelefone) {
    Inputmask({"mask": "(99) 99999-9999"}).mask(campoTelefone);
  }

});
