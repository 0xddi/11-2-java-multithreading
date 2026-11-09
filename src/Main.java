
void main() throws InterruptedException {
    // создаем пул для выполнения наших задач
    // максимальное количество созданных задач - 3
    ThreadPoolExecutor executor = new ThreadPoolExecutor(
            // не изменяйте эти параметры
            3, 3, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(3));

    // сколько задач выполнилось
    AtomicInteger count = new AtomicInteger(0);

    // сколько задач выполняется
    AtomicInteger inProgress = new AtomicInteger(0);

    // отправляем задачи на выполнение
    for (int i = 0; i < 30; i++) {
        final int number = i;
        Thread.sleep(10);
        System.out.println("creating #" + number);


        Runnable task = () -> {
            int working = inProgress.incrementAndGet();
            System.out.println("start #" + number + ", in progress: " + working);
            try {
                Thread.sleep(Math.round(1000 + Math.random() * 2000));
            } catch (InterruptedException e) {
                // ignore
            }
            working = inProgress.decrementAndGet();
            System.out.println("end #" + number + ", in progress: " + working
                    + ", done tasks: " + count.incrementAndGet());
        };

        // если ловим исключение при попытке добавления задачи,
        // то просто ждём пока поток освободится и примет задачу без выброса исключений
        while (true) {
            try {
                executor.submit(task);
                break;
            } catch (RejectedExecutionException e) {
                Thread.sleep(10);
            }
        }
    }

    executor.shutdown();

}
