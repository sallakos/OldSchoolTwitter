//jshint esversion:6

function isTouchDevice() {
  return "ontouchstart" in window || window.DocumentTouch && document instanceof DocumentTouch;
}

// Kuva-painikkeen näyttäminen.
if ($('#pictures').length > 0) {
  var imageStart = $('#pictures').offset().top;
  if (imageStart < $(window).height() && window.innerWidth >= 1200) {
    $('#btn-pictures').addClass('d-none');
  }
}

$(window).resize(function() {
  if ($('#pictures').length > 0) {
    var imageStart = $('#pictures').offset().top;
    if (imageStart < $(window).height() && window.innerWidth >= 1200) {
      $('#btn-pictures').addClass('d-none');
    }
    if (window.innerWidth < 1200) {
      $('#btn-pictures').removeClass('d-none');
    }
  }
});

// Up-painikkeen näyttäminen.
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
$('.go-up').click(function() {
  // Skrollataan sivu oikeaan kohtaan. Haetaan tieto painikkeen id:stä.
  $('html, body').animate({
    scrollTop: 0
  }, 1000);
});

// Bootstrapin tooltip.
$(function() {
  if (!isTouchDevice()) {
    $('[data-toggle="tooltip"]').tooltip({
      trigger: 'hover'
    });
  }
});

// Käyttäjien hakeminen.
$('.usersearch').keyup(function() {
  let input = document.querySelector('[id^="search"]');
  let filter = input.value.toUpperCase();
  const ul = document.getElementById('userList');
  const li = ul.getElementsByClassName('userData');

  if (filter.startsWith('@')) {
    for (let i = 0; i < li.length; i++) {
      let a = li[i].getElementsByTagName('p')[0];
      let txtValue = a.textContent || a.innerText;
      if (txtValue.replace('@ ', '').replace('@', '').toUpperCase().indexOf(filter.replace('@ ', '').replace('@', '')) > -1) {
        li[i].style.display = '';
      } else {
        li[i].style.display = 'none';
      }
    }
  } else {
    for (let i = 0; i < li.length; i++) {
      let a = li[i].getElementsByTagName('h5')[0];
      let txtValue = a.textContent || a.innerText;
      if (txtValue.toUpperCase().indexOf(filter) > -1) {
        li[i].style.display = '';
      } else {
        li[i].style.display = 'none';
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
      $('#allmessages').load('/' + user + ' #allmessagesc', function() {
        updateCommentMessage();
        updateLikeMessage();
      });
      $('#' + user + '-sm textarea').val('');
    }
  });
});

// Lähetetään kommentti, ei päivitetä sivua.
updateCommentMessage = function() {
  $('.commentmessage').submit(function(e) {
    e.preventDefault();
    const id = this.id;
    const target = id.split('-');
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
};
updateCommentMessage(); // Kutsutaan funktiota.

// Lähetetään kommentti, ei päivitetä sivua.
$('.commentpicture').submit(function(e) {
  e.preventDefault();
  const id = this.id;
  const target = id.split('-');
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
updateLikeMessage = function() {
  $('.messagelike').submit(function(e) {
    e.preventDefault();
    const id = this.id;
    const idnro = id.replace('lmf', '');
    const target = $('#lmb' + idnro).val().split('-');
    const user = target[0];
    const title = $('#lmb' + idnro).attr('data-original-title');
    $.ajax({
      url: '/' + user + '/messages/' + idnro + '/like',
      type: 'POST',
      data: $('#' + id).serialize(),
      success: function() {
        $('#lmb' + idnro).load('/' + user + ' #lmb' + idnro + ' i');
        $('#lm' + idnro).load('/' + user + ' #lm' + idnro);
        if (!isTouchDevice()) {
          if (!title.localeCompare('Tykkää')) {
            $('#lmb' + idnro).attr('data-original-title', 'En tykkääkään').tooltip('show');
          } else {
            $('#lmb' + idnro).attr('data-original-title', 'Tykkää').tooltip('show');
          }
        }
      }
    });
  });
};
updateLikeMessage(); // Kutsutaan funktiota.

// Tykätään kuvasta, ei päivitetä sivua.
$('.picturelike').submit(function(e) {
  e.preventDefault();
  const id = this.id;
  const idnro = id.replace('lpf', '');
  const target = $('#lpb' + idnro).val().split('-');
  const user = target[0];
  const title = $('#lpb' + idnro).attr('data-original-title');
  $.ajax({
    url: '/' + user + '/kuvat/' + idnro + '/like',
    type: 'POST',
    data: $('#' + id).serialize(),
    success: function() {
      $('#lpb' + idnro).load('/' + user + '/kuvat #lpb' + idnro + ' i');
      $('#lp' + idnro).load('/' + user + '/kuvat #lp' + idnro);
      if (!isTouchDevice()) {
        if (!title.localeCompare('Tykkää')) {
          $('#lpb' + idnro).attr('data-original-title', 'En tykkääkään').tooltip('show');
        } else {
          $('#lpb' + idnro).attr('data-original-title', 'Tykkää').tooltip('show');
        }
      }
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
        $('#btn-pictures').addClass('d-none');
      }
    }
  });
});

$('.pending-request-button').click(function(e) {
  const id = this.id;
  const target = id.split('-')[2];
  window.location.href = '/' + target;
});

$('.request-accept').submit(function(e) {
  e.preventDefault();
  const id = this.id;
  const target = id.split('-');
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
  const target = id.split('-');
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
  const target = id.split('-');
  const user = target[1];
  const pictureId = target[2];
  $.ajax({
    url: '/' + user + '/kuvat/' + pictureId,
    type: 'DELETE',
    data: $('#' + id).serialize(),
    success: function() {
      console.log('Kuva poistettu.');
    }
  });
});

// Kun klikataan navigoinnin painiketta, skrollataan sivu oikeaan paikkaan.
$('.nav-btn').click(function() {
  // Skrollataan sivu oikeaan kohtaan. Haetaan tieto painikkeen id:stä.
  $('html, body').animate({
    scrollTop: $('#' + $(this).attr('id').substring(4)).offset().top
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
    $('.ps-btn').attr('disabled', true);
  } else {
    $('.size-ins').removeClass('d-none');
    $('.size-alert').addClass('d-none');
    $('.ps-btn').attr('disabled', false);
  }

});
