document.addEventListener('DOMContentLoaded', function () {
    const data = JSON.parse(localStorage.getItem('selectedVitri'));

    if (data) {
        document.getElementById('vitriText').textContent = data.vitri;
        document.getElementById('diemxanhText').textContent = data.diemxanh;
        document.getElementById('diemdoText').textContent = data.diemdo;
    } else {
        console.warn("Không tìm thấy dữ liệu trong localStorage");
    }
});
