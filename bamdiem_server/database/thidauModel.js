const mongoose = require('mongoose');

const Scheme = mongoose.Schema;

const thidauSchema = new mongoose.Schema({
    round: String,
    name_n1: String,
    province_n1: String,
    diem_n1: Number,
    name_n2: String,
    province_n2: String,
    diem_n2: Number,
    minute: Number,
    second: Number,
    weight: String,
    group: String,
    sex: String,
    id_bdiem: { type: Scheme.Types.ObjectId, ref: 'bdiem' },
});
const thidauModel = mongoose.model('thidau', thidauSchema);
module.exports = thidauModel;