//jshint esversion:6

if ($('#pictures').length > 0) {
  var imageStart = $("#pictures").offset().top;
  if (imageStart < $(window).height() && window.innerWidth >= 1200) {
    $("#btn-pictures").addClass("d-none");
  }
}

$(window).resize(function() {
  if ($('#pictures').length > 0) {
    var imageStart = $("#pictures").offset().top;
    if (imageStart < $(window).height() && window.innerWidth >= 1200) {
      $("#btn-pictures").addClass("d-none");
    }
    if (window.innerWidth < 1200) {
      $("#btn-pictures").removeClass("d-none");
    }
  }
})

$(window).scroll(function() {
  if ($('.go-up').length > 0) {
    if ($(window).scrollTop() > $(window).height()) {
      $('.go-up').show();
    } else {
      $('.go-up').hide();
    }
  }
});

// Skrollataan sivun ylös.
$(".go-up").click(function() {
  // Skrollataan sivu oikeaan kohtaan. Haetaan tieto painikkeen id:stä.
  $("html, body").animate({
    scrollTop: 0
  }, 1000);
});

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

  if (filter.startsWith('@')) {
    for (let i = 0; i < li.length; i++) {
      let a = li[i].getElementsByTagName("p")[0];
      let txtValue = a.textContent || a.innerText;
      if (txtValue.replace('@ ', '').replace('@', '').toUpperCase().indexOf(filter.replace('@ ', '').replace('@', '')) > -1) {
        li[i].style.display = "";
      } else {
        li[i].style.display = "none";
      }
    }
  } else {
    for (let i = 0; i < li.length; i++) {
      let a = li[i].getElementsByTagName("a")[0];
      let txtValue = a.textContent || a.innerText;
      if (txtValue.toUpperCase().indexOf(filter) > -1) {
        li[i].style.display = "";
      } else {
        li[i].style.display = "none";
      }
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
  const idnro = target[2];
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
      $('#lms' + idnro).load('/' + user + ' #lms' + idnro);
      $('#lmb' + idnro).load('/' + user + ' #lmb' + idnro + " i");
      $('#lm' + idnro).load('/' + user + ' #lm' + idnro);
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
      $('#lps' + idnro).load('/' + user + '/kuvat #lps' + idnro);
      $('#lpb' + idnro).load('/' + user + '/kuvat #lpb' + idnro + " i");
      $('#lpb' + idnro).button('toggle');
      $('#lp' + idnro).load('/' + user + '/kuvat #lp' + idnro);
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
      if (newStatus === -1) {
        $('#user-container').load('/' + username + ' #user-data');
        $("#btn-pictures").addClass("d-none");
      }
    }
  });
});

$('.pending-request-button').click(function(e) {
  const id = this.id;
  const target = id.split("-")[2];
  window.location.href = '/' + target;
});

$('.request-accept').submit(function(e) {
  e.preventDefault();
  const id = this.id;
  const target = id.split("-");
  const whoToFollow = target[1];
  const whoFollows = target[2];
  $.ajax({
    url: '/' + whoToFollow + '/accept/' + whoFollows,
    type: 'POST',
    data: $('#' + id).serialize(),
    success: function() {
      $('#pru-' + whoToFollow + '-' + whoFollows).remove();
      $('#' + whoToFollow + '-followers').load('/' + whoToFollow + ' #' + whoToFollow + '-followers');
      $('#nr-' + whoToFollow).load('/' + whoToFollow + ' #nr-' + whoToFollow);
    }
  });
});

$('.request-decline').submit(function(e) {
  e.preventDefault();
  const id = this.id;
  const target = id.split("-");
  const whoToFollow = target[1];
  const whoFollows = target[2];
  $.ajax({
    url: '/' + whoToFollow + '/decline/' + whoFollows,
    type: 'POST',
    data: $('#' + id).serialize(),
    success: function() {
      $('#pru-' + whoToFollow + '-' + whoFollows).remove();
    }
  });
});

$('.delete-picture').click(function(e) {
  const id = this.id;
  const target = id.split("-");
  const user = target[1];
  const pictureId = target[2];
  $.ajax({
    url: '/' + user + '/kuvat/' + pictureId,
    type: 'DELETE',
    data: $('#' + id).serialize(),
    success: function() {
      console.log("Kuva poistettu.");
    }
  });
});

// Kun klikataan navigoinnin painiketta, skrollataan sivu oikeaan paikkaan.
$(".nav-btn").click(function() {
  // Skrollataan sivu oikeaan kohtaan. Haetaan tieto painikkeen id:stä.
  $("html, body").animate({
    scrollTop: $("#" + $(this).attr("id").substring(4)).offset().top
  }, 1000);
});

$('.picture-add-from-file').on('change', function(e) {

  let fileSize;

  if (e.currentTarget.files[0] != undefined) {
    fileSize = e.currentTarget.files[0].size;
  } else {
    fileSize = 0;
  }

  if (fileSize >= 1048576) {
    $('.size-ins').addClass('d-none');
    $('.size-alert').removeClass('d-none');
    $('.ps-btn').attr("disabled", true);
  } else {
    $('.size-ins').removeClass('d-none');
    $('.size-alert').addClass('d-none');
    $('.ps-btn').attr("disabled", false);
  }

});
