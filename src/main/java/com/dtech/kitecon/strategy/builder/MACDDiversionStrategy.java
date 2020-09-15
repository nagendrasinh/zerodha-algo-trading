package com.dtech.kitecon.strategy.builder;

import com.dtech.kitecon.data.Instrument;
import com.dtech.kitecon.strategy.TradeDirection;
import org.springframework.stereotype.Component;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighestValueIndicator;
import org.ta4j.core.indicators.helpers.OpenPriceIndicator;
import org.ta4j.core.indicators.pivotpoints.PivotLevel;
import org.ta4j.core.indicators.pivotpoints.PivotPointIndicator;
import org.ta4j.core.indicators.pivotpoints.StandardReversalIndicator;
import org.ta4j.core.indicators.pivotpoints.TimeLevel;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.*;

import java.util.Map;

@Component
public class MACDDiversionStrategy extends BaseStrategyBuilder {

    @Override
    protected Strategy getSellStrategy(Instrument tradingIdentity,
        Map<Instrument, BarSeries> barSeriesMap) {
        return null;
    }

    @Override
    public Strategy getBuyStrategy(Instrument tradingIdentity, Map<Instrument, BarSeries> BarSeriesMap) {
        return create3DaySmaUnderStrategy(BarSeriesMap.get(tradingIdentity));
    }

    @Override
    public TradeDirection getTradeDirection() {
        return TradeDirection.Buy;
    }

    @Override
    public String getName() {
        return "MACDDivergence";
    }

    private static Strategy create3DaySmaUnderStrategy(BarSeries series) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);

        MACDIndicator macdIndicator = new MACDIndicator(closePrice);
        Rule entryRule = new OverIndicatorRule(macdIndicator, 1)
            .and(new UnderIndicatorRule(new RSIIndicator(closePrice, 14), 55))
            .and(new OverIndicatorRule(new RSIIndicator(closePrice, 14), 30));


        Rule exitRule = new StopGainRule(closePrice, 5)
            .or(new StopLossRule(closePrice, 2))
            .or(new TrailingStopLossRule(closePrice, PrecisionNum.valueOf(1)));

        return new BaseStrategy (entryRule, exitRule);
    }

}
