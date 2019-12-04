$(function() {
  $('[data-toggle="tooltip"]').tooltip()
})

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
})
