package com.evbox.everon.ocpp.simulator.station.component.transactionctrlr;

import com.evbox.everon.ocpp.common.CiString;
import com.evbox.everon.ocpp.simulator.station.Station;
import com.evbox.everon.ocpp.simulator.station.StationStore;
import com.evbox.everon.ocpp.simulator.station.component.variable.SetVariableValidator;
import com.evbox.everon.ocpp.simulator.station.component.variable.VariableAccessor;
import com.evbox.everon.ocpp.simulator.station.component.variable.VariableGetter;
import com.evbox.everon.ocpp.simulator.station.component.variable.VariableSetter;
import com.evbox.everon.ocpp.simulator.station.component.variable.attribute.AttributePath;
import com.evbox.everon.ocpp.simulator.station.component.variable.attribute.AttributeType;
import com.evbox.everon.ocpp.v20.message.centralserver.Component;
import com.evbox.everon.ocpp.v20.message.centralserver.GetVariableResult;
import com.evbox.everon.ocpp.v20.message.centralserver.SetVariableResult;
import com.evbox.everon.ocpp.v20.message.centralserver.Variable;
import com.evbox.everon.ocpp.v20.message.station.ReportDatum;
import com.evbox.everon.ocpp.v20.message.station.VariableAttribute;
import com.evbox.everon.ocpp.v20.message.station.VariableCharacteristics;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

import static com.evbox.everon.ocpp.v20.message.station.VariableCharacteristics.DataType.INTEGER;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.isNumeric;

public class EVConnectionTimeOutVariableAccessor extends VariableAccessor {

    public static final String NAME = "EVConnectionTimeOut";
    private final Map<AttributeType, VariableGetter> variableGetters = ImmutableMap.<AttributeType, VariableGetter>builder()
            .put(AttributeType.ACTUAL, this::getActualValue)
            .build();

    private final Map<AttributeType, SetVariableValidator> variableValidators = ImmutableMap.<AttributeType, SetVariableValidator>builder()
            .put(AttributeType.ACTUAL, this::validateActualValue)
            .build();

    private final Map<AttributeType, VariableSetter> variableSetters = ImmutableMap.<AttributeType, VariableSetter>builder()
            .put(AttributeType.ACTUAL, this::setActualValue)
            .build();

    public EVConnectionTimeOutVariableAccessor(Station station, StationStore stationStore) {
        super(station, stationStore);
    }

    @Override
    public String getVariableName() {
        return NAME;
    }

    @Override
    public Map<AttributeType, VariableGetter> getVariableGetters() {
        return variableGetters;
    }

    @Override
    public Map<AttributeType, VariableSetter> getVariableSetters() {
        return variableSetters;
    }

    @Override
    public Map<AttributeType, SetVariableValidator> getVariableValidators() {
        return variableValidators;
    }

    @Override
    public List<ReportDatum> generateReportData(String componentName) {
        Component component = new Component()
                .withName(new CiString.CiString50(componentName));

        int evConnectionTimeOut = getStationStore().getEVConnectionTimeOut();
        VariableAttribute variableAttribute = new VariableAttribute()
                .withValue(new CiString.CiString1000(String.valueOf(evConnectionTimeOut)))
                .withPersistence(false)
                .withConstant(false);

        VariableCharacteristics variableCharacteristics = new VariableCharacteristics()
                .withDataType(INTEGER)
                .withSupportsMonitoring(false);

        ReportDatum reportDatum = new ReportDatum()
                .withComponent(component)
                .withVariable(new Variable().withName(new CiString.CiString50(NAME)))
                .withVariableCharacteristics(variableCharacteristics)
                .withVariableAttribute(singletonList(variableAttribute));

        return singletonList(reportDatum);
    }

    @Override
    public boolean isMutable() { return true; }

    private SetVariableResult validateActualValue(AttributePath attributePath, CiString.CiString1000 attributeValue) {
        SetVariableResult setVariableResult = new SetVariableResult()
                .withComponent(attributePath.getComponent())
                .withVariable(attributePath.getVariable())
                .withAttributeType(SetVariableResult.AttributeType.fromValue(attributePath.getAttributeType().getName()));

        if (!isNumeric(attributeValue.toString())) {
            return setVariableResult.withAttributeStatus(SetVariableResult.AttributeStatus.INVALID_VALUE);
        } else {
            return setVariableResult.withAttributeStatus(SetVariableResult.AttributeStatus.ACCEPTED);
        }
    }

    public void setActualValue(AttributePath attributePath, CiString.CiString1000 attributeValue) {
        Station station = getStation();
        station.updateEVConnectionTimeOut(Integer.parseInt(attributeValue.toString()));
    }

    private GetVariableResult getActualValue(AttributePath attributePath) {
        int evConnectionTimeOut = getStationStore().getEVConnectionTimeOut();

        return new GetVariableResult()
                .withAttributeStatus(GetVariableResult.AttributeStatus.ACCEPTED)
                .withComponent(attributePath.getComponent())
                .withVariable(attributePath.getVariable())
                .withAttributeType(GetVariableResult.AttributeType.fromValue(attributePath.getAttributeType().getName()))
                .withAttributeValue(new CiString.CiString1000(String.valueOf(evConnectionTimeOut)));
    }
}
