package com.senin.leadgen.agent;

/**
 * Pipeline'daki her adımın uyması gereken temel sözleşme.
 * <p>
 * Amaç: her agent'ı bağımsız test edilebilir ve orchestrator içinde
 * birbirinin yerine geçebilir (interchangeable) kılmak.
 *
 * @param <I> agent'ın girdi tipi
 * @param <O> agent'ın çıktı tipi
 */
public interface Agent<I, O> {

    /**
     * Asıl iş mantığı burada. Core logic - implementasyonu sen yazacaksın.
     */
    O execute(I input);

    /**
     * Bu agent'ın adı - loglama ve orchestrator akış takibi için.
     * Default olarak sınıf adını döner, istersen override et.
     */
    default String name() {
        return this.getClass().getSimpleName();
    }
}
