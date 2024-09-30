const mongoose = require('mongoose');

const bdiemSchema = new mongoose.Schema({
    vitri: String,
    tenTT: String,
    passTT: String,
    diemdo: Number,
    diemxanh: Number,
    lichsudo: [
        
    ],
    lichsuxanh: [

    ],
});

const bdiemModel = mongoose.model('bdiem', bdiemSchema);
module.exports = bdiemModel;