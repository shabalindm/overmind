## Единый язык.

**Единый язык** - это коллективный язык, выработанный командой, состоящей
как из экспертов предметной области и, так и из разработчиков;

Вокруг Единого Языка
всегда будут споры, уточнения, исследования. Но результат - кристаллизованное знание.

Единый язык может начинаться в виде словаря, uml - диаграммы. Но полное выражение Языка - в исходном коде системы.
Потом, вероятно, от ведения словаря придется отказаться из соображений экономии. И да, язык - живет и развивается.


![](.ubuqutios-language/Screenshot%20from%202021-12-10%2018-22-56.png)

Код прикладной службы
```java
  public void changeCustomerPersonalName(
            String customerId,
            String customerFirstName,
            String customerLastName) {
        Customer customer = customerRepository.getCutomer(customerId);
        
        if (customer == null) {
            throw new IllegalStateException("Customer does not exist.");
        }
        customer.changePersonalName(customerFirstName, customerLastName);
        customerRepository.saveCutomer(customer);
    }
```