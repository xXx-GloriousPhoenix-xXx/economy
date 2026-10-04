import express from 'express';
import 'dotenv/config';
import { getForecast } from './controllers/weather-controller';

const app = express();
app.use(express.json());

app.post('/api/weather', getForecast);

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`TypeScript Weather API is running on http://localhost:${PORT}`);
});