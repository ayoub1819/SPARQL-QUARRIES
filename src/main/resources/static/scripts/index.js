// Example POST method implementation:
const uploadAlert = document.getElementById('upload-success-alert');
const errorAlert = document.getElementById('upload-danger-alert');
const loader = document.getElementById('loader');

let executeBtn = document.getElementById('execute-query');

executeBtn.addEventListener('click',()=>{

    console.log("..... quaring");
    loader.classList.remove("d-none");
    uploadAlert.classList.add("d-none");
    errorAlert.classList.add("d-none");

    let query = document.getElementById('query-string').value;
    let queryType = document.getElementById('query-type').value;
    
    if(queryType && query){
      let req = {query,queryType};
      //console.log(req);
      executeQuery(req);
    }
})


function executeQuery(req){   
  postData('http://localhost:8080/test', req)
  .then((data) => {
    
    console.log("then :"+data);
    const divShowData = document.getElementById('showData');
    divShowData.innerHTML = "";
    
    if(req.queryType === "ask"){
      let alert = document.createElement("div");
      let alertClass = (data == "yes")? "alert-success":"alert-warning";
      alert.innerText = "the answer is " + data;
      alert.classList.add("alert",alertClass);
      divShowData.appendChild(alert);
    }

    if(req.queryType === "select"){
      let table = jsonToTable(data);
      divShowData.appendChild(table);
    }

    if(req.queryType === "update"){
      let alert = document.createElement("div");
      alert.innerText = "the query is successfully executed";
      alert.classList.add("alert","alert-success");
      divShowData.appendChild(alert);
    }
    
    loader.classList.add("d-none");
    uploadAlert.classList.remove("d-none");
    errorAlert.classList.add("d-none");

}).catch((error) => {
  console.error("eer "+error);
  loader.classList.add("d-none");
  uploadAlert.classList.add("d-none");
  errorAlert.classList.remove("d-none");
});
}



async function postData(url = '', data = {}) {

    // Default options are marked with *
    const response = await fetch(url, {
      method: 'POST', 
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(data) 
    });
    return response.json(); 
  }
  
  




function jsonToTable(data){
  let col = [];

  for (let i = 0; i < data.length; i++) 
      for (let key in data[i]) 
        if (col.indexOf(key) === -1) col.push(key);
  
  const table = document.createElement("table");
  table.classList.add("table","table-striped", "table-hover");

  let tr = table.insertRow(-1);                   
  for (let i = 0; i < col.length; i++) {
      let th = document.createElement("th");      
      th.innerHTML = col[i];
      tr.appendChild(th);
    }
  
    
  for (let i = 0; i < data.length; i++) {

      tr = table.insertRow(-1);

      for (let j = 0; j < col.length; j++) {
        let tabCell = tr.insertCell(-1);
        tabCell.innerHTML = data[i][col[j]];
      }
    }

  return table;

}



        
