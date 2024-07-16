const express = require('express');
const http = require('http');
const WebSocket = require('ws');
const bodyParser = require('body-parser');
const cors = require('cors');
const mongoose = require('mongoose');

const COMMON = require('./database/COMMON');
const bdiemModel = require('./database/bdiemModel');
const thidauModel = require('./database/thidauModel');

const app = express();
const port = 3000;

const server = http.createServer(app);
const wss = new WebSocket.Server({ server });

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(cors());

const router = express.Router();
app.use('/api', router);

const uri = COMMON.uri;

// WebSocket connection
let currentData = {
    round: 'Chưa bắt đầu',
    name_n1: 'Giáp đỏ',
    province_n1: 'Đơn vị',
    name_n2: 'Giáp xanh',
    province_n2: 'Đơn vị',
    minute: 0,
    second: 0,
    diem_n1: 0,
    diem_n2: 0
};

wss.on('connection', (ws) => {
    // Gửi dữ liệu hiện tại cho client mới kết nối
    ws.send(JSON.stringify(currentData));
});

router.post('/view', (req, res) => {
    // Cập nhật currentData với dữ liệu mới từ req.body
    currentData = {
        round: req.body.round || currentData.round,
        name_n1: req.body.name_n1 || currentData.name_n1,
        province_n1: req.body.province_n1 || currentData.province_n1,
        name_n2: req.body.name_n2 || currentData.name_n2,
        province_n2: req.body.province_n2 || currentData.province_n2,
        minute: req.body.minute !== undefined ? req.body.minute : currentData.minute,
        second: req.body.second !== undefined ? req.body.second : currentData.second,
        diem_n1: req.body.diem_n1 !== undefined ? req.body.diem_n1 : currentData.diem_n1,
        diem_n2: req.body.diem_n2 !== undefined ? req.body.diem_n2 : currentData.diem_n2
    };

    // Gửi dữ liệu mới qua WebSocket tới các client
    wss.clients.forEach(client => {
        if (client.readyState === WebSocket.OPEN) {
            client.send(JSON.stringify(currentData));
        }
    });

    res.status(200).json({ message: 'Dữ liệu đã được cập nhật thành công', currentData });
});

router.put('/update-view/:id', (req, res) => {
    const id = req.params.id;
    const updatedData = {
        round: req.body.round || currentData.round,
        name_n1: req.body.name_n1 || currentData.name_n1,
        province_n1: req.body.province_n1 || currentData.province_n1,
        name_n2: req.body.name_n2 || currentData.name_n2,
        province_n2: req.body.province_n2 || currentData.province_n2,
        minute: req.body.minute !== undefined ? req.body.minute : currentData.minute,
        second: req.body.second !== undefined ? req.body.second : currentData.second,
        diem_n1: req.body.diem_n1 !== undefined ? req.body.diem_n1 : currentData.diem_n1,
        diem_n2: req.body.diem_n2 !== undefined ? req.body.diem_n2 : currentData.diem_n2
    };

    // Cập nhật currentData hoặc dữ liệu trong cơ sở dữ liệu của bạn
    currentData = updatedData;

    // Gửi dữ liệu qua WebSocket tới các client
    wss.clients.forEach(client => {
        if (client.readyState === WebSocket.OPEN) {
            client.send(JSON.stringify(updatedData));
        }
    });

    res.status(200).json({ message: 'Dữ liệu đã được cập nhật thành công', data: updatedData });
});




// REST API routes
app.get('/ds_bdiem', async (req, res) => {
    await mongoose.connect(uri);
    let bdiems = await bdiemModel.find();
    console.log(bdiems);
    res.send(bdiems);
});

app.get('/ds_thidau', async (req, res) => {
    await mongoose.connect(uri);
    let data = await thidauModel.find();
    console.log(data);
    res.send(data);
});

router.get('/list', async (req, res) => {
    await mongoose.connect(uri);
    let bdiems = await bdiemModel.find();
    res.send(bdiems);
});

router.post('/add', async (req, res) => {
    await mongoose.connect(uri);
    let bdiem = req.body;
    let kq = await bdiemModel.create(bdiem);
    console.log(kq);
    let bdiems = await bdiemModel.find();
    console.log(bdiems);
    res.send(bdiems);
});

router.put('/updatedo/:id', async (req, res) => {
    try {
        const id = req.params.id;
        const { diemdo } = req.body;
        await mongoose.connect(uri);
        let bdiem = await bdiemModel.findById(id);
        if (!bdiem) {
            return res.status(404).json({ message: 'Không tìm thấy bản ghi' });
        }
        const diemTangThem = diemdo - bdiem.diemdo;
        const historyEntry = `${diemTangThem}`;
        bdiem.lichsudo.push(historyEntry);
        bdiem.diemdo = diemdo;
        await bdiem.save();
        res.json({ message: 'Cập nhật điểm thành công và lưu lịch sử.', bdiem });
    } catch (error) {
        console.error('Lỗi khi cập nhật:', error);
        res.send('Lỗi khi cập nhật');
    }
});

router.put('/updatexanh/:id', async (req, res) => {
    try {
        const id = req.params.id;
        const { diemxanh } = req.body;
        await mongoose.connect(uri);
        let bdiem = await bdiemModel.findById(id);
        if (!bdiem) {
            return res.status(404).json({ message: 'Không tìm thấy bản ghi' });
        }
        const diemTangThem = diemxanh - bdiem.diemxanh;
        const historyEntry = `${diemTangThem}`;
        bdiem.lichsuxanh.push(historyEntry);
        bdiem.diemxanh = diemxanh;
        await bdiem.save(); 
        res.json({ message: 'Cập nhật điểm thành công và lưu lịch sử.', bdiem });
    } catch (error) {
        console.error('Lỗi khi cập nhật:', error);
        res.send('Lỗi khi cập nhật');
    }
});

router.delete('/xoa/:id', async (req, res) => {
    try {
        await mongoose.connect(uri);
        let id = req.params.id;
        console.log(id);
        const result = await bdiemModel.deleteOne({ _id: id });
        if (result) {
            console.log('Xoa thanh cong');
        } else {
            res.send('Xóa không thành công');
        }
    } catch (error) {
        console.error('Lỗi khi xóa:', error);
        res.send('Lỗi khi xóa');
    }
});

router.put('/reset/:id', async (req, res) => {
    try {
        const id = req.params.id;
        const data = {
            diemdo: 0,
            diemxanh: 0,
            lichsudo: [],
            lichsuxanh: [],
        };
        await mongoose.connect(uri);
        const result = await bdiemModel.findByIdAndUpdate(id, data);
        if (result) {
            res.send('Reset thành công');
        } else {
            res.send('Không tìm thấy dữ liệu để reset');
        }
    } catch (error) {
        console.error('Lỗi khi reset:', error);
        res.send('Lỗi khi reset');
    }
});

router.get('/list/:id', async (req, res) => {
    const id = req.params.id;
    try {
        const result = await bdiemModel.findById(id);
        if (result) {
            res.json(result);
        } else {
            res.status(404).send('Không tìm thấy dữ liệu');
        }
    } catch (error) {
        console.error('Lỗi khi lấy dữ liệu:', error);
        res.status(500).send('Lỗi khi lấy dữ liệu');
    }
});

router.get('/list_thidau', async (req, res) => {
    await mongoose.connect(uri);
    let data = await thidauModel.find();
    console.log(data);
    res.send(data);
});

router.post('/add_thidau', async (req, res) => {
    await mongoose.connect(uri);
    let data = req.body;
    let kq = await thidauModel.create(data);
    let data2 = await thidauModel.find();
    console.log(data2);
    res.send(data2);
});

router.delete('/del_thidau/:id', async (req, res) => {
    try {
        await mongoose.connect(uri);
        let id = req.params.id;
        console.log(id);
        const result = await thidauModel.deleteOne({ _id: id });
        if (result) {
            console.log('Xoa thanh cong');
        } else {
            res.send('Xóa không thành công');
        }
    } catch (error) {
        console.error('Lỗi khi xóa:', error);
        res.send('Lỗi khi xóa');
    }
});

router.put('/up_thidau/:id', async (req, res) => {
    try {
        const id = req.params.id;
        const data = req.body;
        await mongoose.connect(uri);
        const result = await thidauModel.findByIdAndUpdate(id, data);
        if (result) {
            let data = await thidauModel.find();
            console.log(data);
            res.send(data);
        } else {
            res.send('Không tìm thấy sản phẩm để cập nhật');
        }
    } catch (error) {
        console.error('Lỗi khi cập nhật:', error);
        res.send('Lỗi khi cập nhật');
    }
});

router.delete('/remove_all', async (req, res) => {
    try {
        await mongoose.connect(uri);
        const result = await thidauModel.deleteMany({});
        if (result.deletedCount > 0) {
            res.send({ message: 'Đã xóa tất cả bản ghi thành công' });
        } else {
            res.send({ message: 'Không có bản ghi nào để xóa' });
        }
    } catch (error) {
        console.error('Lỗi khi xóa tất cả bản ghi:', error);
        res.status(500).send({ message: 'Lỗi khi xóa tất cả bản ghi' });
    }
});

router.get('/list_thidau/:id', async (req, res) => {
    const id = req.params.id;
    try {
        const result = await thidauModel.findById(id);
        if (result) {
            res.json(result);
        } else {
            res.status(404).send('Không tìm thấy dữ liệu');
        }
    } catch (error) {
        console.error('Lỗi khi lấy dữ liệu:', error);
        res.status(500).send('Lỗi khi lấy dữ liệu');
    }
});

// Start the server
server.listen(port, () => {
    console.log(`Server running on port ${port}`);
});