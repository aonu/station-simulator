package com.evbox.everon.ocpp.simulator.station.component.ocppcommctrlr;

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

import java.util.List;
import java.util.Map;

import static com.evbox.everon.ocpp.v20.message.station.VariableCharacteristics.DataType.SEQUENCE_LIST;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.isNumeric;

public class NetworkConfigurationPriorityVariableAccessor extends VariableAccessor {

    public static final String NAME = "NetworkConfigurationPriority";

    NetworkConfigurationPriorityVariableAccessor(Station station, StationStore stationStore) {
        super(station, stationStore);
    }

    private final Map<AttributeType, VariableGetter> variableGetters = Map.of(AttributeType.ACTUAL, this::getActualValue);

    private final Map<AttributeType, SetVariableValidator> variableValidators = Map.of(AttributeType.ACTUAL, this::validateActualValue);

    private final Map<AttributeType, VariableSetter> variableSetters = Map.of(AttributeType.ACTUAL, this::setActualValue);

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

        VariableAttribute variableAttribute = new VariableAttribute()
                .withValue(new CiString.CiString1000(String.valueOf(getStationStore().getNetworkConfigurationPriority())))
                .withPersistence(false)
                .withConstant(false);

        VariableCharacteristics variableCharacteristics = new VariableCharacteristics()
                .withDataType(SEQUENCE_LIST)
                .withSupportsMonitoring(false);

        ReportDatum reportDatum = new ReportDatum()
                .withComponent(component)
                .withVariable(new Variable().withName(new CiString.CiString50(NAME)))
                .withVariableCharacteristics(variableCharacteristics)
                .withVariableAttribute(singletonList(variableAttribute));

        return singletonList(reportDatum);
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    private SetVariableResult validateActualValue(AttributePath attributePath, CiString.CiString1000 attributeValue) {
        SetVariableResult setVariableResult = new SetVariableResult()
                .withComponent(attributePath.getComponent())
                .withVariable(attributePath.getVariable())
                .withAttributeType(SetVariableResult.AttributeType.fromValue(attributePath.getAttributeType().getName()));

        if (!isNumeric(attributeValue.toString())) {
            return setVariableResult.withAttributeStatus(SetVariableResult.AttributeStatus.INVALID_VALUE);
        }

        if (!getStationStore().getNetworkConnectionProfiles().containsKey(Integer.parseInt(attributeValue.toString()))) {
            return setVariableResult.withAttributeStatus(SetVariableResult.AttributeStatus.INVALID_VALUE);
        }

        return setVariableResult.withAttributeStatus(SetVariableResult.AttributeStatus.ACCEPTED);
    }

    private GetVariableResult getActualValue(AttributePath attributePath) {

        List<Integer> networkNetworkConfigurationPriority = getStationStore().getNetworkConfigurationPriority();

        return new GetVariableResult()
                .withAttributeStatus(GetVariableResult.AttributeStatus.ACCEPTED)
                .withComponent(attributePath.getComponent())
                .withVariable(attributePath.getVariable())
                .withAttributeType(GetVariableResult.AttributeType.fromValue(attributePath.getAttributeType().getName()))
                .withAttributeValue(new CiString.CiString1000(String.valueOf(networkNetworkConfigurationPriority)));
    }

    private void setActualValue(AttributePath attributePath, CiString.CiString1000 attributeValue) {
        getStation().updateNetworkConfigurationPriorityValues(Integer.parseInt(attributeValue.toString()));
    }
}
