<script setup>
import { ref, onMounted, computed } from "vue";
import { Line } from "vue-chartjs";
import ChartService from "../service/chartService";
import { Chart, registerables } from "chart.js";
import { useCoinsStore } from "../store/coinsStore";

Chart.register(...registerables);

const coinsStore = useCoinsStore();

// Вземи 4 најпопуларни coins
const topCoins = computed(() => {
  return [...coinsStore.coinDataTop50]
      .sort((a, b) => parseFloat(b.volumeUsd24Hr) - parseFloat(a.volumeUsd24Hr))
      .slice(0, 4);
});

// Боја за секој coin
const coinColors2 = ref({});
const colors = [
  "rgba(75, 192, 192, 0.5)",
  "rgba(54, 162, 235, 0.5)",
  "rgba(255, 99, 132, 0.5)",
  "rgba(255, 206, 86, 0.5)",
];

const selectedCoinData = ref("1DAY"); // Default интервал
const chartData = ref({});
const isLoading = ref(false);

const timeIntervals = [
  { value: "1DAY", label: "1 Hour (1DAY)" },
  { value: "7DAY", label: "1 Week (7DAY)" },
  { value: "1MTH", label: "1 Month (1MTH)" },
];

const chartOptions = {
  responsive: true,
  interaction: {
    mode: "index",
    intersect: true,
  },
  plugins: {
    tooltip: {
      enabled: true,
      mode: "index",
      intersect: true,
      callbacks: {
        label: function (context) {
          return `Price: $${formatPrice(context.parsed.y)}`;
        },
      },
    },
  },
  scales: {
    x: { display: false },
    y: { display: true },
  },
};

const fetchChartData = async () => {
  isLoading.value = true;
  chartData.value = {}; // Очисти претходни податоци

  for (let i = 0; i < topCoins.value.length; i++) {
    const coin = topCoins.value[i];

    try {
      const response = await ChartService.getChart(coin.id, selectedCoinData.value);
      const chartArray = response.data?.data;

      if (chartArray && chartArray.length > 0) {
        const prices = chartArray.map((entry) => ({
          date: new Date(entry.date).toLocaleTimeString(),
          price: parseFloat(entry.priceUsd),
        }));

        chartData.value[coin.id] = {
          labels: prices.map((entry) => entry.date),
          datasets: [
            {
              label: `${coin.symbol} Цена во USD`,
              backgroundColor: coinColors2.value[coin.id].backgroundColor,
              borderColor: coinColors2.value[coin.id].borderColor,
              borderWidth: 2,
              fill: true,
              data: prices.map((entry) => entry.price),
            },
          ],
        };
      } else {
        console.error(`No chart data for ${coin.id}`);
      }
    } catch (error) {
      console.error("Error fetching chart data for " + coin.id, error);
    }

    // Мал delay помеѓу барањата за да се избегне rate limit
    if (i < topCoins.value.length - 1) {
      await new Promise((resolve) => setTimeout(resolve, 300));
    }
  }

  isLoading.value = false;
};

const handleIntervalChange = () => {
  fetchChartData();
};

onMounted(() => {
  topCoins.value.forEach((coin, index) => {
    coinColors2.value[coin.id] = {
      backgroundColor: colors[index],
      borderColor: colors[index].replace("0.5", "1"),
    };
  });

  fetchChartData();
});

function formatPrice(price) {
  if (price >= 1.01) return price.toFixed(2);
  const [_, decimals] = price.toString().split(".");
  if (!decimals) return price.toFixed(2);
  const firstNonZeroIndex = decimals.search(/[^0]/);
  if (firstNonZeroIndex >= 4) return price.toFixed(8);
  else if (firstNonZeroIndex >= 2) return price.toFixed(4);
  else return price.toFixed(2);
}
</script>

<template>
  <div class="bg-[#1B2028] rounded-[10px] p-[20px] w-full flex flex-wrap justify-around">
    <h1 class="font-bold text-white w-full text-center mb-4 text-3xl">
      Today's most popular coins chart
    </h1>

    <!-- Dropdown за временски интервал -->
    <div class="w-full flex justify-center mb-6">
      <div class="flex items-center gap-3">
        <label for="timeInterval" class="text-white font-semibold text-lg">
          Time Interval:
        </label>
        <select
            id="timeInterval"
            v-model="selectedCoinData"
            @change="handleIntervalChange"
            class="bg-[#2A3139] text-white px-4 py-2 rounded-lg border border-gray-600 focus:outline-none focus:border-blue-500 cursor-pointer hover:bg-[#343D47] transition-colors"
        >
          <option
              v-for="interval in timeIntervals"
              :key="interval.value"
              :value="interval.value"
          >
            {{ interval.label }}
          </option>
        </select>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="isLoading" class="w-full flex justify-center items-center py-10">
      <div class="text-white text-xl">Loading charts...</div>
    </div>

    <!-- Charts -->
    <div v-if="!isLoading" class="w-full flex flex-wrap justify-around">
      <div v-for="coin in topCoins" :key="coin.id" class="w-[45%] mb-6">
        <h2 class="text-white font-bold text-center mb-2">{{ coin.symbol }} Price</h2>
        <Line
            v-if="chartData[coin.id] && chartData[coin.id].labels.length > 0"
            :data="chartData[coin.id]"
            :options="chartOptions"
        />
        <p v-else class="text-white text-center">
          Error fetching chart data for {{ coin.symbol }}
        </p>
      </div>
    </div>
  </div>
</template>
