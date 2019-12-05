// Bootstrapin tooltip.
$(function() {
  $('[data-toggle="tooltip"]').tooltip()
});

// Käyttäjien hakeminen.
$('.usersearch').keyup(function() {
  let input = document.querySelector('[id^="search"]')
  let filter = input.value.toUpperCase();
  const ul = document.getElementById("userList")
  const li = ul.getElementsByTagName('div')

  for (let i = 0; i < li.length; i++) {
    let a = li[i].getElementsByTagName("a")[0];
    let txtValue = a.textContent || a.innerText;
    if (txtValue.toUpperCase().indexOf(filter) > -1) {
      li[i].style.display = "";
    } else {
      li[i].style.display = "none";
    }
  }
});

// Lähetetään oma viesti, ei päivitetä sivua.
$('.messagesend').submit(function(e) {
  e.preventDefault();
  const id = this.id;
  const user = id.replace('-sm', '');
  $.ajax({
    url: '/' + user + '/messages',
    type: 'POST',
    data: $('#' + id).serialize(),
    success: function() {
      $('#allmessages').load('/' + user + ' #allmessages');
      $('#' + user + '-sm textarea').val('');
    }
  });
});

// Lähetetään kommentti, ei päivitetä sivua.
$('.commentmessage').submit(function(e) {
  e.preventDefault();
  const id = this.id;
  const target = id.split("-");
  const user = target[0];
  const idnro = target[2]
  $.ajax({
    url: '/' + user + '/messages/' + idnro + '/comment',
    type: 'POST',
    data: $('#' + id).serialize(),
    success: function() {
      $('#c-' + idnro).load('/' + user + ' #c-' + idnro);
      $('#' + id + ' textarea').val('');
    }
  });
});

// Lähetetään kommentti, ei päivitetä sivua.
$('.commentpicture').submit(function(e) {
  e.preventDefault();
  const id = this.id;
  const target = id.split("-");
  const user = target[0];
  const idnro = target[2];
  $.ajax({
    url: '/' + user + '/kuvat/' + idnro + '/comment',
    type: 'POST',
    data: $('#' + id).serialize(),
    success: function() {
      $('#c-' + idnro).load('/' + user + '/kuvat #c-' + idnro);
      $('#' + id + ' textarea').val('');
    }
  });
});

// Tykätään viestistä, ei päivitetä sivua.
$('.messagelike').submit(function(e) {
  e.preventDefault();
  const id = this.id;
  const idnro = id.replace('lmf', '');
  const target = $('#lmb' + idnro).val().split("-");
  const user = target[0];
  $.ajax({
    url: '/' + user + '/messages/' + idnro + '/like',
    type: 'POST',
    data: $('#' + id).serialize(),
    success: function() {
      $('#lms' + idnro).load('/' + user + ' #lms' + idnro)
      $('#lm' + idnro).load('/' + user + ' #lm' + idnro)
    }
  });
});

// Tykätään kuvasta, ei päivitetä sivua.
$('.picturelike').submit(function(e) {
  e.preventDefault();
  const id = this.id;
  const idnro = id.replace('lpf', '');
  const target = $('#lpb' + idnro).val().split("-");
  const user = target[0];
  $.ajax({
    url: '/' + user + '/kuvat/' + idnro + '/like',
    type: 'POST',
    data: $('#' + id).serialize(),
    success: function() {
      $('#lps' + idnro).load('/' + user + '/kuvat #lps' + idnro)
      $('#lp' + idnro).load('/' + user + '/kuvat #lp' + idnro)
    }
  });
});

// Seurataan henkilöä. Ei uudelleenohjausta millekään sivulle.
$('.follow').submit(function(e) {
  e.preventDefault();
  const id = this.id;
  const username = id.replace('f_', '');
  let url = '/' + username + '/';
  let newStatus = -100;
  let newText = '';
  let pageText = '';
  const buttonText = $('#fb_' + username + ' span').text();
  if (!buttonText.localeCompare('Seuraa')) {
    newStatus = 0;
    newText = 'Peru pyyntö';
    pageText = 'Näet käyttäjän viestit ja kuvat, kun hän on hyväksynyt seuraamispyyntösi.';
    url += 'follow';
  } else {
    newStatus = -1;
    newText = 'Seuraa';
    pageText = 'Seuraa käyttäjää nähdäksesi hänen viestinsä ja kuvansa.';
    url += 'unfollow';
  }
  $.ajax({
    url: url,
    type: 'POST',
    data: $('#' + id).serialize(),
    success: function() {
      $('#fb_' + username + ' span').text(newText);
      $('#pending').text(pageText);
    }
  });
});

// Kun klikataan navigoinnin painiketta, skrollataan sivu oikeaan paikkaan. Lisäksi vaihdetaan painikkeelle merkintä aktiivinen.
$(".nav-btn").click(function() {

  // // Klikkaus tapahtui. Asetetaan targetiksi se, mitä klikattiin.
  // clicked = true;
  // targetSection = $(this).attr("id").substring(4);
  //
  // // Poistetaan kaikista painikkeista aktiivisuus ja lisätään se tietylle.
  // $(".nav-btn").attr("disabled", false);
  // $("#btn-" + targetSection).attr("disabled", true);

  // Skrollataan sivu oikeaan kohtaan. Haetaan tieto painikkeen id:stä.
  $("html, body").animate({
    scrollTop: $("#" + $(this).attr("id").substring(4)).offset().top
  }, 1000);

  // Klikattaessa ei anneta muiden navigointipainikkeiden muuttua. Animaatio kestää 1000 ms, jonka ajan klikkaus on voimassa.
  window.setTimeout(function() {
    clicked = false;
  }, 1000);

});
