const host = config;
let currentAction = '';

const socket = new WebSocket(`ws://${config.host}:${config.port}`);

socket.onopen = function() {
    console.log('WebSocket connection opened');
};

socket.onmessage = function(event) {
    const data = JSON.parse(event.data);
    console.log("Received data: ", data);

    if (data.action === 'statusUpdate') {
        console.log("Status Update Action Received: ", data.isOn);
        return;
    }

    if (data.action === 'resetAll') {
        document.getElementById('round').textContent = 'Chưa bắt đầu';
        document.getElementById('name_n1').textContent = 'Giáp đỏ';
        document.getElementById('province_n1').textContent = 'Đơn vị';
        document.getElementById('name_n2').textContent = 'Giáp xanh';
        document.getElementById('province_n2').textContent = 'Đơn vị';
        document.getElementById('time').textContent = '00:00';
        document.getElementById('diem_n1').textContent = '0';
        document.getElementById('diem_n2').textContent = '0';
        hienThi();
        return;
    }

    if (data.action === 'updateTime') {
        console.log("Received updateTime action: ");
        if (data.minute !== undefined && data.second !== undefined) {
            console.log("Minute: ", data.minute, "Second: ", data.second);
            document.getElementById('time').textContent = `${String(data.minute).padStart(2, '0')}:${String(data.second).padStart(2, '0')}`;
            
            if (data.minute === 0 && data.second === 10) {
                console.log("10 giây còn lại - Phát âm thanh!");
                sound.play().then(() => {
                    console.log("Sound played successfully");
                }).catch(error => {
                    console.error("Error playing sound: ", error);
                });
            }
        }
    }else {
        if (data.name_n1 !== undefined) {
            document.getElementById('name_n1').textContent = data.name_n1;
        }
        if (data.province_n1 !== undefined) {
            document.getElementById('province_n1').textContent = data.province_n1;
        }
        if (data.name_n2 !== undefined) {
            document.getElementById('name_n2').textContent = data.name_n2;
        }
        if (data.province_n2 !== undefined) {
            document.getElementById('province_n2').textContent = data.province_n2;
        }
        if (data.minute !== undefined && data.second !== undefined) {
            document.getElementById('time').textContent = `${String(data.minute).padStart(2, '0')}:${String(data.second).padStart(2, '0')}`;
        }
        if (data.diem_n1 !== undefined) {
            document.getElementById('diem_n1').textContent = data.diem_n1;
        }
        if (data.diem_n2 !== undefined) {
            document.getElementById('diem_n2').textContent = data.diem_n2;
        }
    }
};

socket.onerror = function(error) {
    console.log('WebSocket Error: ' + error);
};

socket.onclose = function() {
    console.log('WebSocket connection closed');
};

const hienThi = async () => {
    const tbody = document.querySelector('#tbody');

    try {
        const api = await fetch(`http://${config.host}:${config.port}/api/list`);
        if (!api.ok) {
            throw new Error(`HTTP error! Status: ${api.status}`);
        }
        const data = await api.json();
        console.log(data);

        
        tbody.innerHTML = '';

        data.forEach(item => {
            const tr = document.createElement('tr');

            const tdVitri = document.createElement('td');
            tdVitri.textContent = item.vitri;
            tdVitri.classList.add('vitri');

            const tdDiemdo = document.createElement('td');
            tdDiemdo.textContent = item.diemdo;
            tdDiemdo.classList.add('diemdo');

            const tdDiemxanh = document.createElement('td');
            tdDiemxanh.textContent = item.diemxanh;
            tdDiemxanh.classList.add('diemxanh');
            
            const tdLichsu = document.createElement('td');
            tdLichsu.classList.add('lichsu');

            const tdLichsuDo = document.createElement('span');
            tdLichsuDo.textContent = item.lichsudo;
            tdLichsuDo.classList.add('lichsu-do');
            const tdLichsuXanh = document.createElement('span');
            tdLichsuXanh.textContent = item.lichsuxanh;
            tdLichsuXanh.classList.add('lichsu-xanh');
            tdLichsu.appendChild(tdLichsuDo);
            tdLichsu.appendChild(document.createElement('br'));
            tdLichsu.appendChild(tdLichsuXanh);
                                                
            const tdKetQua = document.createElement('td');
            const ketQua = item.diemxanh > item.diemdo ? 'Xanh thắng' : (item.diemxanh < item.diemdo ? 'Đỏ thắng' : 'Hòa');
            // tdKetQua.textContent = ketQua;
            tdKetQua.classList.add('ketqua');
            if (ketQua === 'Xanh thắng') {
                tdKetQua.classList.add('result-blue');
            } else if (ketQua === 'Đỏ thắng') {
                tdKetQua.classList.add('result-red');
            } else {
                tdKetQua.classList.add('result-draw');
            }

            const tdTT = document.createElement('td');
            tdTT.classList.add('thaotac');

            const btnReset = document.createElement('button');
            btnReset.textContent = 'Reset';
            btnReset.classList.add('btn', 'btn-secondary', 'btn_reset', 'btn_xoa');
            btnReset.addEventListener('click', async () => {  
                const res = confirm(`Do you want to reset position ${item.vitri} ?`);
                if (res) {
                    try {
                        const response = await fetch(`http://${config.host}:${config.port}/api/reset/${item._id}`, {
                            method: 'PUT'
                        });

                        if (response.ok) {
                            alert('Reset thành công');
                            hienThi();
                        } else {
                            throw new Error('Reset không thành công');
                        }
                    } catch (error) {
                        console.error('Lỗi khi reset:', error);
                        alert('Lỗi khi reset');
                    }
                }
            });
            
            const btnXoa = document.createElement('button');
            btnXoa.textContent = 'Remove';
            btnXoa.classList.add('btn', 'btn-dark', 'btn_xoa');
            btnXoa.addEventListener('click', async () => {  
                try {
                    const conf = confirm(`Do you want to delete this position ${item.vitri} ?`);
                    if (conf) {
                        const response = await fetch(`http://${config.host}:${config.port}/api/xoa/${item._id}`, {
                        method: 'DELETE'
                    });

                    if (!response.ok) {
                        throw new Error(`HTTP error! Status: ${response.status}`);
                    }

                    // Xóa hàng từ DOM
                    tr.remove();
                    alert('Xóa thành công');
                    }
            
                } catch (error) {
                    console.error('Lỗi khi xóa vị trí:', error);
                }
            });

            const btnTangd= document.createElement('button');
            btnTangd.textContent = '+';
            btnTangd.classList.add('btn', 'btn-danger', 'btn_xoa');
            btnTangd.addEventListener('click', async () => {
                const newScoreTD = item.diemdo + 1;
                    try {
                        await fetch(`http://${config.host}:${config.port}/api/updatedo/${item._id}`, {
                            method: 'PUT',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify({ diemdo: newScoreTD }),
                        });
                    } catch (error) {
                        console.error('Error updating red score:', error);
                    }
                                
                });

            const btnGiamd= document.createElement('button');
            btnGiamd.textContent = '-';
            btnGiamd.classList.add('btn', 'btn-danger', 'btn_xoa');
            btnGiamd.addEventListener('click', async () => {
                let newScoreGD = item.diemdo - 1;
                if (newScoreGD >= 0) {
                    try {
                        await fetch(`http://${config.host}:${config.port}/api/updatedo/${item._id}`, {
                            method: 'PUT',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify({ diemdo: newScoreGD }),
                        });
                    } catch (error) {
                        console.error('Error updating red score:', error);
                    }
                                
                } else{
                    alert('Không giảm được nữa !!');
                }
            });
            
            const btnTangx= document.createElement('button');
            btnTangx.textContent = '+';
            btnTangx.classList.add('btn', 'btn-primary', 'btn_xoa');
            btnTangx.addEventListener('click', async () => {
                const newScoreTX = item.diemxanh + 1;
                    try {
                        await fetch(`http://${config.host}:${config.port}/api/updatexanh/${item._id}`, {
                            method: 'PUT',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify({ diemxanh: newScoreTX }),
                        });
                    } catch (error) {
                        console.error('Error updating red score:', error);
                    }               
                });

                const btnGiamx= document.createElement('button');
                btnGiamx.textContent = '-';
                btnGiamx.classList.add('btn', 'btn-primary', 'btn_xoa');
                btnGiamx.addEventListener('click', async () => {
                    let newScoreGX = item.diemxanh - 1;
                    if (newScoreGX >= 0) {
                        try {
                            await fetch(`http://${config.host}:${config.port}/api/updatexanh/${item._id}`, {
                                method: 'PUT',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify({ diemxanh: newScoreGX }),
                            });
                        } catch (error) {
                            console.error('Error updating red score:', error);
                        }
                                    
                    } else{
                        alert('Không giảm được nữa !!');
                    }
                });    

            tr.appendChild(tdVitri);
            tr.appendChild(tdDiemdo);
            tr.appendChild(tdDiemxanh);
            tr.appendChild(tdLichsu);
            tr.appendChild(tdKetQua);
            tr.appendChild(tdTT);  

            tdTT.appendChild(btnXoa);
            tdTT.appendChild(btnReset);
            tdTT.appendChild(btnTangd);
            tdTT.appendChild(btnGiamd);
            tdTT.appendChild(btnTangx); 
            tdTT.appendChild(btnGiamx);

    
            tbody.appendChild(tr);
        });
    } catch (error) {
        console.error('Fetch error:', error);
    }
}
hienThi();
setInterval(hienThi, 100);

const dialog = ()=>{
    var dialog = document.getElementById("dialog");

    var openDialogBtn = document.getElementById("openDialogBtn");

    var closeBtn = document.querySelector("#dialog .close");

    openDialogBtn.onclick = function() {
        dialog.style.display = "block";
    }

    closeBtn.onclick = function() {
        dialog.style.display = "none";
    }

    window.onclick = function(event) {
        if (event.target == dialog) {
            dialog.style.display = "none";
        }
    }

};
dialog();

const themVitri = () => {
    document.getElementById('addPositionForm').addEventListener('submit', async (event) => {
    event.preventDefault();

    const formData = new FormData(event.target);

    const vitri = formData.get('vitri');
    const diemdo = 0;
    const diemxanh = 0;

    try {
        const response = await fetch(`http://${config.host}:${config.port}/api/add`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ vitri, diemdo, diemxanh })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const data = await response.json();
        alert('Thêm vị trí thành công !');
        console.log('Vị trí mới đã được thêm:', data);

    } catch (error) {
        console.error('Lỗi khi thêm vị trí mới:', error);
    }
});
}
themVitri();

document.getElementById('resetAllBtn').addEventListener('click', async () => {
    try {
        const conf = confirm('Do you want to reset all locations ?');
        if(conf){
            const api = await fetch(`http://${config.host}:${config.port}/api/list`);
            if (!api.ok) {
                throw new Error(`HTTP error! Status: ${api.status}`);
            }
            const data = await api.json();
            console.log(data);

            data.forEach(async (item) => {
                try {
                    const response = await fetch(`http://${config.host}:${config.port}/api/reset/${item._id}`, {
                        method: 'PUT'
                    });

                    if (!response.ok) {
                        throw new Error(`HTTP error! Status: ${response.status}`);
                    }
                } catch (error) {
                    console.error('Lỗi khi reset:', error);
                    alert('Lỗi khi reset');
                }
            });
            if (webSocket && webSocket.readyState === WebSocket.OPEN) {
                webSocket.send(JSON.stringify({ action: 'resetAll' }));
            }

            alert('Reset tất cả các điểm thành công');
        }
    } catch (error) {
    console.error('Fetch error:', error);
    }
});

document.addEventListener('DOMContentLoaded', () => {
const savedState = localStorage.getItem('toggleState');
const checkbox = document.getElementById('onoff');

if (savedState === 'on') {
    checkbox.checked = true;
} else if (savedState === 'off') {
    checkbox.checked = false;
}

const host = config.host;
const port = config.port;
const webSocket = new WebSocket(`ws://${host}:${port}/ws`);

webSocket.onopen = () => {
    console.log('WebSocket is connected.');
};

webSocket.onmessage = (event) => {
    const message = JSON.parse(event.data);
    console.log('Received message:', message);

    if (message.action === 'resetAll') {
        console.log('Reset all points.');
    }
    if (message.action === 'statusUpdate') {
        const isOn = message.isOn;
        const checkbox = document.getElementById('onoff');
        if (checkbox) {
            checkbox.checked = isOn;
        }
    }
    if (message.action === 'updateScores') {
        document.querySelectorAll('#tbody tr').forEach(row => {
            if (row.querySelector('.vitri').textContent === data.vitri) {
                row.querySelector('.diemdo').textContent = data.diemdo;
                row.querySelector('.diemxanh').textContent = data.diemxanh;
            }
        });
    }
};

webSocket.onclose = () => {
    console.log('WebSocket connection closed. Attempting to reconnect...');
    setTimeout(() => {
        webSocket = new WebSocket(`ws://${host}:${port}/ws`);
    }, 5000); 
};

webSocket.onerror = (error) => {
    console.error('WebSocket error:', error);
};

document.getElementById('onoff').addEventListener('change', (event) => {
    const isChecked = event.target.checked;
    localStorage.setItem('toggleState', isChecked ? 'on' : 'off');
    const action = isChecked ? 'on' : 'off';

    if (webSocket && webSocket.readyState === WebSocket.OPEN) {
        webSocket.send(JSON.stringify({ action }));
    }
});
});

document.getElementById('tangdAll').addEventListener('click', async () => {
try {
const api = await fetch(`http://${config.host}:${config.port}/api/list`);
if (!api.ok) {
    throw new Error(`HTTP error! Status: ${api.status}`);
}
const data = await api.json();

const updatePromises = data.map(async (item) => {
    const newRedScore = item.diemdo + 1;

    const respone = await fetch(`http://${config.host}:${config.port}/api/updatedo/${item._id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ diemdo: newRedScore }),
    });
});


await Promise.all(updatePromises);

} catch (error) {
console.error('Fetch error:', error);
}
});

document.getElementById('giamdAll').addEventListener('click', async () => {
try {
    const api = await fetch(`http://${config.host}:${config.port}/api/list`);
    if (!api.ok) {
        throw new Error(`HTTP error! Status: ${api.status}`);
    }
    const data = await api.json();


    data.forEach(async (item) => {
        const currentRedScore = item.diemdo;
        if (currentRedScore == 0) {
        
        }
        const newRedScore = Math.max(currentRedScore - 1, 0);

        try {
            if (newRedScore !== currentRedScore) {
                await fetch(`http://${config.host}:${config.port}/api/updatedo/${item._id}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ diemdo: newRedScore }),
                });
            }
            console.log(`Scores updated for position ${item.vitri}`);

        } catch (error) {
            console.error('Error during fetch:', error);
        }
    });

} catch (error) {
    console.error('Fetch error:', error);
}
});

document.getElementById('tangxAll').addEventListener('click', async () => {
    try {
    const api = await fetch(`http://${config.host}:${config.port}/api/list`);
    if (!api.ok) {
        throw new Error(`HTTP error! Status: ${api.status}`);
    }
    const data = await api.json();

    data.forEach(async (item) => {
        const newBlueScore = item.diemxanh + 1;
        try {
            await fetch(`http://${config.host}:${config.port}/api/updatexanh/${item._id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ diemxanh: newBlueScore }),
            });

            console.log(`Scores updated for position ${item.vitri}`);

        } catch (error) {
            console.error('Error during fetch:', error);
        }
    });

    } catch (error) {
    console.error('Fetch error:', error);
    }
});

document.getElementById('giamxAll').addEventListener('click', async () => {
try {
    const api = await fetch(`http://${config.host}:${config.port}/api/list`);
    if (!api.ok) {
        throw new Error(`HTTP error! Status: ${api.status}`);
    }
    const data = await api.json();


    data.forEach(async (item) => {
        const currentBlueScore = item.diemxanh;
        if (currentBlueScore == 0) {
        
        }
        const newBlueScore = Math.max(currentBlueScore - 1, 0);

        try {
            if (newBlueScore !== currentBlueScore) {
                await fetch(`http://${config.host}:${config.port}/api/updatexanh/${item._id}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ diemxanh: newBlueScore }),
                });
            }
            console.log(`Scores updated for position ${item.vitri}`);

        } catch (error) {
            console.error('Error during fetch:', error);
        }
    });

} catch (error) {
    console.error('Fetch error:', error);
}
});

document.addEventListener("keydown", function(event) {
    if (event.code === "Space") {
        event.preventDefault();
        const checkbox = document.getElementById("onoff");
        
        if (checkbox) {
            checkbox.checked = !checkbox.checked;
            checkbox.dispatchEvent(new Event('change')); 
        }
    }
    if (event.code === "Delete"){
        event.preventDefault();
        const resetAll = document.getElementById('resetAllBtn');
        if (resetAll) {
            resetAll.dispatchEvent(new Event('click'));
        }
    }
    if (event.code === 'ArrowLeft'){
        event.preventDefault();
        const tangdAll = document.getElementById('tangdAll');
        if (tangdAll){
            tangdAll.dispatchEvent(new Event('click'));
        }
    }
    if (event.code === 'ArrowRight'){
        event.preventDefault();
        const giamAll = document.getElementById('giamdAll');
        if (giamdAll){
            giamdAll.dispatchEvent(new Event('click'));
        }
    }
    if (event.code === 'ArrowUp'){
        event.preventDefault();
        const tangxAll = document.getElementById('tangxAll');
        if (tangxAll){
            tangxAll.dispatchEvent(new Event('click'));
        }
    }
    if (event.code === 'ArrowDown'){
        event.preventDefault();
        const giamAll = document.getElementById('giamxAll');
        if (giamxAll){
            giamxAll.dispatchEvent(new Event('click'));
        }
    }
});