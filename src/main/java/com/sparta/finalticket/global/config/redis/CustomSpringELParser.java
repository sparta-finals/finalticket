package com.sparta.finalticket.global.config.redis;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class CustomSpringELParser {
    private CustomSpringELParser() {
    }

    public static Object getDynamicValue(String[] parameterNames, Object[] args, String[] keys) { //parameterName:매개변수이름, args:매개변수실제값, key:평가할려는SpEL표현식
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        StringBuilder combinedKey = new StringBuilder();
        for (String key : keys) {
            if (combinedKey.length() > 0) {
                combinedKey.append(":");
            }
            combinedKey.append(parser.parseExpression(key).getValue(context, String.class));;
        }

        return combinedKey.toString();
    }
}
