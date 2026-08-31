package com.senin.leadgen.agent;

/**
 * Bir agent adımının başarılı mı yoksa başarısız mı bittiğini taşıyan
 * sonuç tipi. Orchestrator'ın "bir adım patlarsa zinciri durdur / atla"
 * kararını verebilmesi için exception fırlatmak yerine bunu tercih ettik.
 */
public record AgentResult<T>(boolean success, T value, String errorMessage) {

    public static <T> AgentResult<T> ok(T value) {
        return new AgentResult<>(true, value, null);
    }

    public static <T> AgentResult<T> failed(String errorMessage) {
        return new AgentResult<>(false, null, errorMessage);
    }
}
