"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Progress } from "@/components/ui/progress";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Checkbox } from "@/components/ui/checkbox";

type Step = "name" | "birthdate" | "conditions";

const conditionsList = ["고혈압", "당뇨", "고지혈증", "관절염"];

export default function OnboardingProfilePage() {
  const [step, setStep] = useState<Step>("name");
  const [formData, setFormData] = useState({
    name: "",
    birthdate: "",
    gender: "",
    conditions: [] as string[],
  });
  const router = useRouter();

  const handleNext = () => {
    if (step === "name") setStep("birthdate");
    else if (step === "birthdate") setStep("conditions");
    else if (step === "conditions") router.push("/onboarding/device");
  };

  const handleBack = () => {
    if (step === "birthdate") setStep("name");
    else if (step === "conditions") setStep("birthdate");
  };
  
  const handleConditionChange = (condition: string) => {
    setFormData(prev => ({
        ...prev,
        conditions: prev.conditions.includes(condition)
            ? prev.conditions.filter(c => c !== condition)
            : [...prev.conditions, condition]
    }))
  }

  const progressValue =
    step === "name" ? 33 : step === "birthdate" ? 66 : 100;

  return (
    <Card className="w-full">
      <CardHeader>
        <Progress value={progressValue} className="mb-4 h-2" />
        {step === "name" && <CardTitle>이름을 알려주세요</CardTitle>}
        {step === "birthdate" && <CardTitle>생년월일을 알려주세요</CardTitle>}
        {step === "conditions" && <CardTitle>건강 정보를 알려주세요</CardTitle>}
        <CardDescription>맞춤 관리를 위해 필요해요.</CardDescription>
      </CardHeader>
      <CardContent>
        <div className="space-y-6">
          {step === "name" && (
            <div className="space-y-2">
              <Label htmlFor="name" className="text-base">이름</Label>
              <Input
                id="name"
                placeholder="김철수"
                value={formData.name}
                onChange={(e) =>
                  setFormData({ ...formData, name: e.target.value })
                }
                autoComplete="name"
              />
            </div>
          )}
          {step === "birthdate" && (
            <div className="space-y-2">
              <Label htmlFor="birthdate" className="text-base">생년월일 8자리</Label>
              <Input
                id="birthdate"
                placeholder="19501024"
                value={formData.birthdate}
                onChange={(e) =>
                  setFormData({ ...formData, birthdate: e.target.value })
                }
                type="tel"
                maxLength={8}
              />
            </div>
          )}
          {step === "conditions" && (
            <div className="space-y-6">
              <div className="space-y-3">
                <Label className="text-base">성별</Label>
                <RadioGroup
                  defaultValue={formData.gender}
                  onValueChange={(value) => setFormData({...formData, gender: value})}
                  className="flex gap-4"
                >
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="male" id="male" />
                    <Label htmlFor="male" className="text-base">남성</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="female" id="female" />
                    <Label htmlFor="female" className="text-base">여성</Label>
                  </div>
                </RadioGroup>
              </div>
              <div className="space-y-3">
                 <Label className="text-base">가지고 있는 질환을 모두 선택해주세요.</Label>
                 <div className="grid grid-cols-2 gap-4">
                    {conditionsList.map(condition => (
                        <div key={condition} className="flex items-center space-x-3 rounded-md border p-4">
                            <Checkbox id={condition} checked={formData.conditions.includes(condition)} onCheckedChange={() => handleConditionChange(condition)} />
                            <Label htmlFor={condition} className="text-base font-normal">{condition}</Label>
                        </div>
                    ))}
                 </div>
              </div>
            </div>
          )}
        </div>
        <div className="mt-8 flex gap-4">
          {step !== "name" && (
            <Button size="xl" variant="outline" onClick={handleBack} className="w-1/3">
              이전
            </Button>
          )}
          <Button size="xl" onClick={handleNext} className="w-full">
            {step === "conditions" ? "거의 다 왔어요!" : "다음"}
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}
