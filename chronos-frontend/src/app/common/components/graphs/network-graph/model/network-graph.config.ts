export interface NetworkGraphConfig {
  text: {
      color: string,
      fontSize: number,
      fontWeight: number
  },
  links: {
      color: string,
      width: number,
      opacity: number,
      text: {
        color: string,
        fontSize: number
      }
  },
  nodes: {
      radius: number,
      stroke: string,
      strokeWidth: number,
      dim: number
  },
  zoom: {
    enabled: boolean
  },
  drag: {
    enabled: boolean
  }
};

export const defaultNetworkGraphConfig: NetworkGraphConfig = {
  text: {
      color: "#eee",  // "#0f172a",
      fontSize: 11,
      fontWeight: 300
  },
  links: {
      color: "#94a3b8",
      width: 1.5,
      opacity: 0.7,
      text: {
        color: "#eee",  // "#64748b",
        fontSize: 9
      }
  },
  nodes: {
      radius: 28,
      stroke: "#eee", // "#0f172a",
      strokeWidth: 3,
      dim: 1
  },
  zoom: {
    enabled: false
  },
  drag: {
    enabled: false
  }
};