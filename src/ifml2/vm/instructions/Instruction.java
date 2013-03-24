package ifml2.vm.instructions;

import ifml2.IFML2Exception;
import ifml2.om.IFMLObject;
import ifml2.om.Item;
import ifml2.om.Location;
import ifml2.vm.ExpressionCalculator;
import ifml2.vm.IFML2VMException;
import ifml2.vm.RunningContext;
import ifml2.vm.VirtualMachine;
import ifml2.vm.values.BooleanValue;
import ifml2.vm.values.CollectionValue;
import ifml2.vm.values.ObjectValue;
import ifml2.vm.values.Value;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

public abstract class Instruction implements Cloneable
{
	@XmlAttribute(name="position")
	public int position;
	
	@XmlTransient
	public VirtualMachine virtualMachine; // links

    @Override
    public Instruction clone() throws CloneNotSupportedException
    {
        Instruction clone = (Instruction) super.clone();
        clone.virtualMachine = virtualMachine; // just link
        return clone;
    }

    abstract public void run(RunningContext runningContext) throws IFML2Exception;

    private void validateParameterForNull(String parameterValue, String instructionTitle, Object parameterName) throws IFML2VMException
    {
        if(parameterValue == null || "".equals(parameterValue))
        {
            throw new IFML2VMException("Параметр {0} не задан у инструкции [{1}]", parameterName, instructionTitle);
        }
    }

    IFMLObject getObjectFromExpression(String expression, RunningContext runningContext, String instructionTitle, Object parameterName,
                                       boolean objectCanBeNull) throws IFML2Exception
    {
        validateParameterForNull(expression, instructionTitle, parameterName);

        Value itemValue = ExpressionCalculator.calculate(runningContext, expression);

        if(!(itemValue instanceof ObjectValue))
        {
            throw new IFML2VMException("Тип выражения ({0}) – не Объект у инструкции [{1}]", expression, instructionTitle);
        }

        IFMLObject object = ((ObjectValue) itemValue).value;

        // test for null
        if(!objectCanBeNull && object == null)
        {
            throw new IFML2VMException("Объект " + expression + " не найден");
        }

        return object;
    }

    Item getItemFromExpression(String expression, RunningContext runningContext, String instructionTitle, Object parameterName,
                                         boolean objectCanBeNull) throws IFML2Exception
    {
        IFMLObject object = getObjectFromExpression(expression, runningContext, instructionTitle, parameterName, objectCanBeNull);

        if(objectCanBeNull && object == null)
        {
            return null;
        }

        if(!(object instanceof Item))
        {
            throw new IFML2VMException("Тип выражения ({0}) – не Предмет у инструкции {1}", expression, instructionTitle);
        }

        return (Item) object;
    }

    Location getLocationFromExpression(String expression, RunningContext runningContext, String instructionTitle,
                                       Object parameterName, boolean objectCanBeNull) throws IFML2Exception
    {
        IFMLObject object = getObjectFromExpression(expression, runningContext, instructionTitle, parameterName, objectCanBeNull);

        if(objectCanBeNull && object == null)
        {
            return null;
        }

        if(!(object instanceof Location))
        {
            throw new IFML2VMException("Тип выражения ({0}) – не Локация у инструкции {1}", expression, instructionTitle);
        }

        return (Location) object;
    }

    boolean getBooleanFromExpression(String expression, RunningContext runningContext, String instructionTitle,
                                     Object parameterName) throws IFML2Exception
    {
        validateParameterForNull(expression, instructionTitle, parameterName);

        Value boolValue = ExpressionCalculator.calculate(runningContext, expression);

        if(!(boolValue instanceof BooleanValue))
        {
            throw new IFML2VMException("Тип выражения ({0}) – не Логическое у инструкции [{1}]", expression, instructionTitle);
        }

        return ((BooleanValue) boolValue).value;
    }

    List<?> getCollectionFromExpression(String expression, RunningContext runningContext, String instructionTitle,
                                     Object parameterName) throws IFML2Exception
    {
        validateParameterForNull(expression, instructionTitle, parameterName);

        Value collectionValue = ExpressionCalculator.calculate(runningContext, expression);

        if(!(collectionValue instanceof CollectionValue))
        {
            throw new IFML2VMException("Тип выражения ({0}) – не Коллекция у инструкции [{1}]", expression, instructionTitle);
        }

        return ((CollectionValue) collectionValue).getValue();
    }
}
