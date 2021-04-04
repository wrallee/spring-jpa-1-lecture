package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne(ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    // ... 무한루프 발생
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        return all;
    }

    // ... N+1 문제 발생
    @GetMapping("/api/v2/simple-orders")
    public Result orderV2() {
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<OrderSimpleQueryDto> collect = orders.stream()
                .map(OrderSimpleQueryDto::new)
                .collect(Collectors.toList());

        return new Result(collect);
    }

    // 가장 권장되는 방식! 필요 시 fetch join을 사용하여 성능을 최적화 한다.
    @GetMapping("/api/v3/simple-orders")
    public Result orderV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<OrderSimpleQueryDto> collect = orders.stream()
                .map(OrderSimpleQueryDto::new)
                .collect(Collectors.toList());
        return new Result(collect);
    }

    @GetMapping("/api/v4/simple-orders")
    public Result orderV4() {
        // 성능면에서는 좋지만 Repository에서 DTO로 직접 받는 것은 재사용성이 조금 떨어진다
        // 필요할 경우 아래와 같이 Repository를 분리하여 활용한다
        return new Result(orderSimpleQueryRepository.findOrderDtos());
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        List<T> data;
    }

}
