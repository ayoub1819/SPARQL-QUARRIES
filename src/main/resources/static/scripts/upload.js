const uploadBtn = document.getElementById('btn-upload');

uploadBtn.addEventListener('click',upload);



function upload(){
    console.log('Uploading...');
    loader.classList.remove("d-none");
    uploadAlert.classList.add("d-none");

    const formData = new FormData();
    const files = document.querySelector('input[type="file"][multiple]');


for (const file of files.files) 
  formData.append(`files`, file);


console.log(formData);
console.log(formData.getAll("files"));

//////////////////////////////////////////////////
fetch('http://localhost:8080/upload', {
  method: 'POST',
  body: formData,
})
  .then((response) => response.json())
  .then((result) => {
    console.log('Success:', result);
    loader.classList.add("d-none");
    uploadAlert.classList.remove("d-none");
  })
  .catch((error) => {
    console.error('Error:', error);
  });


}






