# BlueCore Minecraft Plugin

A complete economy, ranks, and shop system for PaperMC 1.20.4.

## Features

### Economy System
- UUID-based balance storage
- Persistent YAML storage
- Thread-safe operations
- Commands: `/bal`, `/pay`
- Configurable starting/max balance
- Sell values for blocks/items

### Rank System
- Config-driven ranks with prefixes
- Priority-based system
- TAB list integration
- Scoreboard-based display
- Commands: `/rank set`, `/rank get`

### Shop System
- Inventory GUI with categories
- Buy & sell functionality
- Shift-click for stacks
- Sound feedback
- Config-driven items & prices

## Installation

1. Download the latest JAR from releases
2. Place in your server's `plugins/` folder
3. Restart/reload the server
4. Configure files in `plugins/BlueCore/`

## Configuration

### Economy (economy.yml)
```yaml
starting-balance: 1000.0
max-balance: 1000000.0
currency-symbol: "$"
sell-values:
  DIAMOND: 250.0
  GOLD_INGOT: 50.0