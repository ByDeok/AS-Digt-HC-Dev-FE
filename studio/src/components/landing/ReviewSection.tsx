import React from 'react';
import { Star, User, Quote } from 'lucide-react';
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
} from '@/components/ui/carousel';
import { Card, CardContent } from '@/components/ui/card';

interface Review {
  id: number;
  name: string;
  age: string;
  role: 'Senior' | 'Caregiver' | 'Family' | 'User';
  rating: number;
  content: string;
  highlight?: string;
}

const REVIEWS: Review[] = [
  // Best Reviews (Persona Based)
  { id: 1, name: '김철수', age: '68세', role: 'Senior', rating: 5, content: '예전에는 병원 갈 때마다 혈압 수첩 챙기고 설명하느라 진땀을 뺐는데, 이제는 "1장 요약 리포트"만 출력해서 가져갑니다. 의사 선생님도 좋아하시네요.', highlight: '병원 갈 때 이것만 챙깁니다' },
  { id: 2, name: '이지은', age: '42세', role: 'Caregiver', rating: 5, content: '부산에 계신 엄마 건강이 늘 걱정이었는데, 가족 공유 보드로 엄마가 오늘 미션을 완료하셨는지 바로 알 수 있어 안심이 됩니다. 감시가 아니라 연결된 기분이에요.', highlight: '멀리 사는 엄마 걱정이 확 줄었어요' },
  { id: 3, name: '박영자', age: '75세', role: 'Senior', rating: 5, content: '글씨가 큼직큼직해서 돋보기 없이도 다 보입니다. 3분 만에 시작하기 기능 덕분에 복잡한 가입 없이 금방 시작했어요. 하루 3개 미션이라 숙제 같지 않고 재밌습니다.', highlight: '돋보기 안 써도 잘 보입니다' },
  { id: 4, name: '최성민', age: '63세', role: 'Senior', rating: 5, content: '갤럭시 워치랑 연동하니까 활동량이랑 혈압 변화를 분석해서 오늘의 행동 카드로 딱 짚어줍니다. 시키는 대로만 걸었더니 컨디션이 훨씬 좋아졌습니다.', highlight: '워치 연동이 기가 막히네요' },
  
  // General Reviews
  { id: 5, name: '정민호', age: '34세', role: 'Caregiver', rating: 5, content: '아버지 병원 동행을 못 해 드릴 때 리포트 PDF를 의사 선생님께 보여드리라고 했습니다. 나중에 리포트만 봐도 알 수 있어 정말 유용합니다. 효도템이네요.', highlight: '병원 동행 못 할 때 필수' },
  { id: 6, name: '한소희', age: '52세', role: 'User', rating: 5, content: '부모님 깔아드리기 전에 먼저 해봤는데, 회원가입부터 연동까지 진짜 3분도 안 걸리네요. 어르신들 쓰기 딱 좋은 군더더기 없는 앱입니다.', highlight: '진짜 3분 컷이네요?' },
  { id: 7, name: '강명석', age: '70세', role: 'Senior', rating: 4, content: '다른 앱들은 새벽에도 울려서 잠을 깨우곤 했는데, 이건 잘 때는 조용하고 아침에 할 일을 알려주니 점잖아서 좋습니다.', highlight: '밤에 알림 안 울려서 좋아요' },
  { id: 8, name: '오말순', age: '69세', role: 'Senior', rating: 5, content: '내가 오늘 걷기 미션 성공하면 며느리랑 손자가 앱에서 박수 쳐주니 신나서 더 걷게 돼요. 칭찬을 들으니 건강 관리할 맛이 납니다.', highlight: '가족들의 응원이 힘이 됩니다' },
  { id: 9, name: '익명', age: '65세', role: 'Senior', rating: 4, content: '당뇨 수첩 삐뚤빼뚤 적어가면 선생님이 보기 힘들어하셨는데, 이건 깔끔하게 정리해주니 진료 시간이 확 줄었어요.', highlight: '수첩보다 백배 낫습니다' },
  { id: 10, name: '김민준', age: '24세', role: 'Family', rating: 5, content: '할머니 폰에 깔아드렸는데, 주말에 가니까 "나 오늘 미션 다 했다"면서 자랑하시네요. UI가 직관적이라 혼자서도 잘 쓰십니다.', highlight: '할머니가 먼저 자랑하세요' },

  // Short Reviews (Additional)
  { id: 11, name: '이영희', age: '58세', role: 'User', rating: 5, content: '복잡한 기능 없이 딱 필요한 것만 있어서 좋아요.', highlight: '심플해서 좋습니다' },
  { id: 12, name: '박준형', age: '45세', role: 'Caregiver', rating: 5, content: '부모님 혈압 변화를 한눈에 볼 수 있어 안심입니다.', highlight: '데이터 확인이 편해요' },
  { id: 13, name: '최미숙', age: '66세', role: 'Senior', rating: 5, content: '매일 아침 오늘의 미션 확인하는 게 습관이 됐어요.', highlight: '하루의 시작' },
  { id: 14, name: '정상훈', age: '38세', role: 'Family', rating: 4, content: '가족들이 다 같이 건강 챙기는 분위기가 됐어요.', highlight: '가족 분위기 업' },
  { id: 15, name: '김동원', age: '72세', role: 'Senior', rating: 5, content: '글씨가 커서 시원시원합니다. 노안이어도 문제없네요.', highlight: '큰 글씨 최고' },
  { id: 16, name: '유진', age: '29세', role: 'Family', rating: 5, content: '할아버지 선물로 스마트워치랑 같이 해드렸는데 대만족!', highlight: '최고의 선물' },
  { id: 17, name: '박성훈', age: '55세', role: 'User', rating: 4, content: 'PDF로 저장해서 보관하기 편합니다.', highlight: '기록 보관 용이' },
  { id: 18, name: '이순자', age: '71세', role: 'Senior', rating: 5, content: '자식들이랑 연결되어 있다는 느낌이 참 좋네요.', highlight: '외롭지 않아요' },
  { id: 19, name: '장민호', age: '48세', role: 'Caregiver', rating: 5, content: '약 먹을 시간 알려주는 게 아니라 행동을 제안해줘서 좋아요.', highlight: '잔소리 대신 코칭' },
  { id: 20, name: '김영숙', age: '64세', role: 'Senior', rating: 5, content: '친구들한테도 다 깔아줬어요. 다들 좋아합니다.', highlight: '친구 추천 1순위' },
  { id: 21, name: '송혜교', age: '33세', role: 'Family', rating: 5, content: '엄마 건강 리포트 볼 때마다 마음이 놓여요.', highlight: '효녀 필수템' },
  { id: 22, name: '박철민', age: '67세', role: 'Senior', rating: 4, content: '입력이 간편해서 귀찮지 않습니다.', highlight: '간편한 입력' },
  { id: 23, name: '최자영', age: '60세', role: 'User', rating: 5, content: '건강 관리가 숙제가 아니라 놀이처럼 느껴져요.', highlight: '즐거운 건강관리' },
  { id: 24, name: '정우성', age: '41세', role: 'Caregiver', rating: 5, content: '응급 알림 기능 덕분에 마음 놓고 일합니다.', highlight: '안전지킴이' },
  { id: 25, name: '한지민', age: '36세', role: 'Family', rating: 5, content: '할머니가 매일 걷기 운동을 시작하셨어요!', highlight: '행동 변화' },
  { id: 26, name: '김해숙', age: '73세', role: 'Senior', rating: 5, content: '병원 갈 때 의사 선생님이 칭찬해 주셨어요.', highlight: '의사 선생님 인정' },
  { id: 27, name: '이정재', age: '49세', role: 'User', rating: 4, content: '데이터가 쌓이니 내 건강 흐름이 보이네요.', highlight: '데이터의 힘' },
  { id: 28, name: '박보영', age: '31세', role: 'Family', rating: 5, content: '부모님이랑 대화 주제가 늘었어요.', highlight: '소통의 창구' },
  { id: 29, name: '조인성', age: '44세', role: 'Caregiver', rating: 5, content: '멀리 떨어져 살아도 곁에 있는 것 같아요.', highlight: '거리 극복' },
  { id: 30, name: '윤여정', age: '76세', role: 'Senior', rating: 5, content: '이 나이에 스마트한 노인이 된 기분입니다.', highlight: '스마트 시니어' },
];

export function ReviewSection() {
  return (
    <section id="reviews" className="mx-auto w-full max-w-7xl px-4 py-16 sm:px-6 lg:px-8 bg-muted/20">
      <div className="text-center space-y-4 mb-12">
        <h2 className="text-2xl font-bold tracking-tight text-foreground sm:text-3xl lg:text-4xl break-keep">
          이미 많은 가족들이<br className="sm:hidden" /> 안심하고 있습니다
        </h2>
        <p className="text-base sm:text-lg text-muted-foreground max-w-2xl mx-auto break-keep">
          시니어, 보호자, 가족 모두가 만족하는<br className="sm:hidden" /> 골든 웰니스의 생생한 후기를 확인하세요.
        </p>
      </div>

      <Carousel
        opts={{
          align: 'start',
          loop: true,
        }}
        className="w-full max-w-6xl mx-auto"
      >
        <CarouselContent className="-ml-2 md:-ml-4">
          {REVIEWS.map((review) => (
            <CarouselItem key={review.id} className="pl-2 md:pl-4 md:basis-1/2 lg:basis-1/3">
              <Card className="h-full border-0 bg-background shadow-sm hover:shadow-md transition-shadow">
                <CardContent className="flex flex-col h-full p-6">
                  <div className="flex items-start justify-between mb-4">
                    <div className="flex items-center gap-2">
                      <div className="h-8 w-8 rounded-full bg-primary/10 flex items-center justify-center text-primary">
                        <User className="h-4 w-4" />
                      </div>
                      <div>
                        <p className="text-sm font-semibold text-foreground">{review.name}</p>
                        <p className="text-xs text-muted-foreground">{review.age} · {review.role}</p>
                      </div>
                    </div>
                    <div className="flex text-yellow-400">
                      {[...Array(review.rating)].map((_, i) => (
                        <Star key={i} className="h-4 w-4 fill-current" />
                      ))}
                    </div>
                  </div>
                  
                  {review.highlight && (
                    <div className="mb-3">
                      <span className="inline-block rounded-md bg-primary/5 px-2 py-1 text-xs font-medium text-primary break-keep">
                        "{review.highlight}"
                      </span>
                    </div>
                  )}

                  <div className="relative flex-1">
                    <Quote className="absolute -top-2 -left-2 h-6 w-6 text-muted-foreground/10 rotate-180" />
                    <p className="text-sm text-muted-foreground leading-relaxed pl-2 break-keep">
                      {review.content}
                    </p>
                  </div>
                </CardContent>
              </Card>
            </CarouselItem>
          ))}
        </CarouselContent>
        <div className="flex items-center justify-center gap-2 mt-8 md:hidden">
          {/* 모바일에서는 하단에 컨트롤러 배치 (옵션) */}
        </div>
        <div className="hidden md:block">
          <CarouselPrevious className="-left-12 lg:-left-16" />
          <CarouselNext className="-right-12 lg:-right-16" />
        </div>
      </Carousel>
    </section>
  );
}
