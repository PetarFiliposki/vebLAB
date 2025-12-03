
class ChartService {
  static getChart(coin, period) {
    return new Promise((resolve) => {
      const now = Date.now();
      const data = [];

      for (let i = 0; i < 10; i++) {
        data.push({
          date: now - i * 3600 * 1000,
          priceUsd: (Math.random() * 100).toFixed(2),
        });
      }

      resolve({ data: { data } });
    });
  }
}

export default ChartService;