const mongoose = require('mongoose');

const bdiemSchema = new mongoose.Schema({
    vitri: String,
    diemdo: Number,
    diemxanh: Number,
    lichsudo: [
        
    ],
    lichsuxanh: [

    ],
});

const bdiemModel = mongoose.model('bdiem', bdiemSchema);
module.exports = bdiemModel;